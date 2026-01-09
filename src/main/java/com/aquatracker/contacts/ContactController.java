package com.aquatracker.contacts;

import com.aquatracker.common.IdMapper;
import com.aquatracker.user.User;
import com.aquatracker.user.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Profile("!dev")
@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactController(ContactRepository contactRepository,
                            UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    /**
     * Pobiera listę kontaktów dla użytkownika
     * GET /api/v1/contacts/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getContacts(@PathVariable String userId) {
        try {
            String userIdString = IdMapper.fromUserId(userId);
            if (userIdString == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID format (expected UUID)"));
            }

            List<Contact> contacts = contactRepository.findByUser_Id(userIdString);
            List<ContactResponseDto> contactDtos = contacts.stream()
                    .map(ContactResponseDto::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(contactDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch contacts: " + e.getMessage()));
        }
    }

    /**
     * Wysyła zaproszenie do kontaktu
     * POST /api/v1/contacts/{userId}
     * Body: { "email": "email@example.com" }
     * Tworzy dwa rekordy Contact:
     * - user=sender, friend=recipient, status="sent"
     * - user=recipient, friend=sender, status="pending"
     */
    @PostMapping("/{userId}")
    @Transactional
    public ResponseEntity<?> sendInvitation(@PathVariable String userId, @RequestBody Map<String, String> request) {
        try {
            String recipientEmail = request.get("email");

            if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "email is required"));
            }

            String senderId = IdMapper.fromUserId(userId);
            if (senderId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID format (expected UUID)"));
            }

            User sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new RuntimeException("Sender not found"));

            // Sprawdź czy użytkownik nie wysyła zaproszenia do siebie
            if (sender.getEmail().equalsIgnoreCase(recipientEmail.trim())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cannot send invitation to yourself"));
            }

            // Znajdź odbiorcę po emailu
            User recipient = userRepository.findByEmail(recipientEmail.trim())
                    .orElseThrow(() -> new RuntimeException("Recipient not found in system"));

            // Sprawdź czy już istnieje kontakt (jako friend)
            if (contactRepository.existsByUser_IdAndFriend_Id(senderId, recipient.getId())) {
                Optional<Contact> existingContact = contactRepository.findByUser_IdAndFriend_Id(senderId, recipient.getId());
                if (existingContact.isPresent() && "friend".equals(existingContact.get().getStatus())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("error", "Contact already exists as friend"));
                }
            }

            // Sprawdź czy już istnieje zaproszenie (sent/pending)
            Optional<Contact> existingSent = contactRepository.findByUser_IdAndFriend_IdAndStatus(
                    senderId, recipient.getId(), "sent");
            Optional<Contact> existingPending = contactRepository.findByUser_IdAndFriend_IdAndStatus(
                    recipient.getId(), senderId, "pending");
            
            if (existingSent.isPresent() || existingPending.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Invitation already sent"));
            }

            // Utwórz pierwszy rekord Contact: sender -> recipient ze statusem "sent"
            Contact contact1 = new Contact();
            contact1.setUser(sender);
            contact1.setFriend(recipient);
            contact1.setFriendName(recipient.getUsername());
            contact1.setFriendEmail(recipient.getEmail());
            contact1.setStatus("sent");
            contact1.setCreatedAt(LocalDateTime.now());
            contactRepository.save(contact1);

            // Utwórz drugi rekord Contact: recipient -> sender ze statusem "pending"
            Contact contact2 = new Contact();
            contact2.setUser(recipient);
            contact2.setFriend(sender);
            contact2.setFriendName(sender.getUsername());
            contact2.setFriendEmail(sender.getEmail());
            contact2.setStatus("pending");
            contact2.setCreatedAt(LocalDateTime.now());
            contactRepository.save(contact2);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Invitation sent successfully",
                            "contact", new ContactResponseDto(contact1)));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send invitation: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send invitation: " + e.getMessage()));
        }
    }

    /**
     * Akceptuje zaproszenie
     * POST /api/v1/contacts/{userId}/accept/{contactId}
     * Zmienia status obu rekordów Contact na "friend"
     */
    @PostMapping("/{userId}/accept/{contactId}")
    @Transactional
    public ResponseEntity<?> acceptInvitation(@PathVariable String userId, @PathVariable String contactId) {
        try {
            String userIdString = IdMapper.fromUserId(userId);
            if (userIdString == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID format (expected UUID)"));
            }

            Long contactIdLong = IdMapper.fromContactId(contactId);
            if (contactIdLong == null) {
                try {
                    contactIdLong = Long.parseLong(contactId);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid contact ID format"));
                }
            }

            // Znajdź rekord Contact z statusem "pending"
            Contact pendingContact = contactRepository.findById(contactIdLong)
                    .orElseThrow(() -> new RuntimeException("Contact not found"));

            // Sprawdź czy użytkownik jest właścicielem tego kontaktu
            if (!pendingContact.getUser().getId().equals(userIdString)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only accept invitations sent to you"));
            }

            // Sprawdź czy status to "pending"
            if (!"pending".equals(pendingContact.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Contact is not pending"));
            }

            User recipient = pendingContact.getUser();
            User sender = pendingContact.getFriend();

            // Znajdź drugi rekord Contact (sender -> recipient ze statusem "sent")
            Contact sentContact = contactRepository.findByUser_IdAndFriend_Id(sender.getId(), recipient.getId())
                    .orElseThrow(() -> new RuntimeException("Corresponding contact not found"));

            if (!"sent".equals(sentContact.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Corresponding contact is not in sent status"));
            }

            // Zmień status obu rekordów na "friend"
            pendingContact.setStatus("friend");
            sentContact.setStatus("friend");
            contactRepository.save(pendingContact);
            contactRepository.save(sentContact);

            return ResponseEntity.ok(Map.of("message", "Invitation accepted successfully",
                    "contact", new ContactResponseDto(pendingContact)));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to accept invitation: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to accept invitation: " + e.getMessage()));
        }
    }

    /**
     * Odrzuca/usuwuje zaproszenie
     * DELETE /api/v1/contacts/{userId}/invitation/{contactId}
     * Usuwa oba rekordy Contact (pending i sent)
     */
    @DeleteMapping("/{userId}/invitation/{contactId}")
    @Transactional
    public ResponseEntity<?> rejectInvitation(@PathVariable String userId, @PathVariable String contactId) {
        try {
            String userIdString = IdMapper.fromUserId(userId);
            if (userIdString == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID format (expected UUID)"));
            }

            Long contactIdLong = IdMapper.fromContactId(contactId);
            if (contactIdLong == null) {
                try {
                    contactIdLong = Long.parseLong(contactId);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid contact ID format"));
                }
            }

            // Znajdź rekord Contact
            Contact contact = contactRepository.findById(contactIdLong)
                    .orElseThrow(() -> new RuntimeException("Contact not found"));

            // Sprawdź czy użytkownik jest właścicielem tego kontaktu lub jego przyjacielem
            boolean isOwner = contact.getUser().getId().equals(userIdString);
            boolean isFriend = contact.getFriend().getId().equals(userIdString);
            
            if (!isOwner && !isFriend) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only delete your own invitations"));
            }

            // Sprawdź czy status to "pending" lub "sent"
            if (!"pending".equals(contact.getStatus()) && !"sent".equals(contact.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Can only delete pending or sent invitations"));
            }

            User user1 = contact.getUser();
            User user2 = contact.getFriend();

            // Usuń oba rekordy Contact
            contactRepository.deleteByUser_IdAndFriend_Id(user1.getId(), user2.getId());
            contactRepository.deleteByUser_IdAndFriend_Id(user2.getId(), user1.getId());

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reject invitation: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reject invitation: " + e.getMessage()));
        }
    }

    /**
     * Usuwa kontakt (friend)
     * DELETE /api/v1/contacts/{userId}/friend/{contactId}
     * Usuwa oba rekordy Contact ze statusem "friend"
     */
    @DeleteMapping("/{userId}/friend/{contactId}")
    @Transactional
    public ResponseEntity<?> removeFriend(@PathVariable String userId, @PathVariable String contactId) {
        try {
            String userIdString = IdMapper.fromUserId(userId);
            if (userIdString == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID format (expected UUID)"));
            }

            Long contactIdLong = IdMapper.fromContactId(contactId);
            if (contactIdLong == null) {
                try {
                    contactIdLong = Long.parseLong(contactId);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid contact ID format"));
                }
            }

            // Znajdź rekord Contact
            Contact contact = contactRepository.findById(contactIdLong)
                    .orElseThrow(() -> new RuntimeException("Contact not found"));

            // Sprawdź czy użytkownik jest właścicielem tego kontaktu
            if (!contact.getUser().getId().equals(userIdString)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only delete your own contacts"));
            }

            // Sprawdź czy status to "friend"
            if (!"friend".equals(contact.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Can only delete friend contacts"));
            }

            User user1 = contact.getUser();
            User user2 = contact.getFriend();

            // Usuń oba rekordy Contact
            contactRepository.deleteByUser_IdAndFriend_Id(user1.getId(), user2.getId());
            contactRepository.deleteByUser_IdAndFriend_Id(user2.getId(), user1.getId());

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove contact: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove contact: " + e.getMessage()));
        }
    }

    /**
     * Pobiera listę zaproszeń dla użytkownika (pending i sent)
     * GET /api/v1/contacts/{userId}/invitations
     */
    @GetMapping("/{userId}/invitations")
    public ResponseEntity<?> getInvitations(@PathVariable String userId) {
        try {
            String userIdString = IdMapper.fromUserId(userId);
            if (userIdString == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID format (expected UUID)"));
            }

            // Pobierz zaproszenia oczekujące (pending) i wysłane (sent)
            List<Contact> pendingInvitations = contactRepository.findByUser_IdAndStatus(userIdString, "pending");
            List<Contact> sentInvitations = contactRepository.findByUser_IdAndStatus(userIdString, "sent");

            List<ContactResponseDto> allInvitations = pendingInvitations.stream()
                    .map(ContactResponseDto::new)
                    .collect(Collectors.toList());
            
            allInvitations.addAll(sentInvitations.stream()
                    .map(ContactResponseDto::new)
                    .toList());

            return ResponseEntity.ok(allInvitations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch invitations: " + e.getMessage()));
        }
    }
}

