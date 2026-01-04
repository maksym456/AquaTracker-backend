package com.aquatracker.contacts;

import com.aquatracker.Invitation;
import com.aquatracker.InvitationRepository;
import com.aquatracker.InvitationResponseDto;
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
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;

    public ContactController(ContactRepository contactRepository,
                            InvitationRepository invitationRepository,
                            UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Pobiera listę kontaktów dla użytkownika
     * GET /api/v1/contacts/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getContacts(@PathVariable String userId) {
        try {
            Long userIdLong = IdMapper.fromUserId(userId);
            if (userIdLong == null) {
                // Jeśli userId nie jest w formacie u_123, traktuj jako bezpośredni ID (dla kompatybilności z mock)
                try {
                    userIdLong = Long.parseLong(userId);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid user ID format"));
                }
            }

            List<Contact> contacts = contactRepository.findByUserId(userIdLong);
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
     * POST /api/v1/contacts/invitations
     * Body: { "senderId": "u_123", "recipientEmail": "email@example.com" }
     */
    @PostMapping("/invitations")
    @Transactional
    public ResponseEntity<?> sendInvitation(@RequestBody Map<String, String> request) {
        try {
            String senderIdStr = request.get("senderId");
            String recipientEmail = request.get("recipientEmail");

            if (senderIdStr == null || recipientEmail == null || recipientEmail.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "senderId and recipientEmail are required"));
            }

            Long senderId = IdMapper.fromUserId(senderIdStr);
            if (senderId == null) {
                try {
                    senderId = Long.parseLong(senderIdStr);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid senderId format"));
                }
            }

            User sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new RuntimeException("Sender not found"));

            // Sprawdź czy użytkownik nie wysyła zaproszenia do siebie
            if (sender.getEmail().equalsIgnoreCase(recipientEmail.trim())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cannot send invitation to yourself"));
            }

            // Sprawdź czy już istnieje kontakt
            Optional<User> recipientOpt = userRepository.findByEmail(recipientEmail.trim());
            if (recipientOpt.isPresent()) {
                User recipient = recipientOpt.get();
                if (contactRepository.existsByUserIdAndFriendId(senderId, recipient.getId())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("error", "Contact already exists"));
                }
            }

            // Sprawdź czy już istnieje zaproszenie pending
            Optional<Invitation> existingInvitation = invitationRepository
                    .findBySenderIdAndRecipientEmail(senderId, recipientEmail.trim());
            if (existingInvitation.isPresent() && 
                "pending".equals(existingInvitation.get().getStatus())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Invitation already sent"));
            }

            // Utwórz nowe zaproszenie
            Invitation invitation = new Invitation();
            invitation.setSender(sender);
            invitation.setRecipientEmail(recipientEmail.trim());
            invitation.setStatus("pending");
            invitation.setCreatedAt(LocalDateTime.now());

            // Jeśli odbiorca istnieje w bazie, ustaw go
            recipientOpt.ifPresent(invitation::setRecipient);

            invitation = invitationRepository.save(invitation);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new InvitationResponseDto(invitation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send invitation: " + e.getMessage()));
        }
    }

    /**
     * Akceptuje zaproszenie
     * POST /api/v1/contacts/invitations/{invitationId}/accept
     */
    @PostMapping("/invitations/{invitationId}/accept")
    @Transactional
    public ResponseEntity<?> acceptInvitation(@PathVariable String invitationId) {
        try {
            Long invId = IdMapper.fromInvitationId(invitationId);
            if (invId == null) {
                try {
                    invId = Long.parseLong(invitationId);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid invitation ID format"));
                }
            }

            Invitation invitation = invitationRepository.findById(invId)
                    .orElseThrow(() -> new RuntimeException("Invitation not found"));

            if (!"pending".equals(invitation.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invitation is not pending"));
            }

            User sender = invitation.getSender();
            User recipient = invitation.getRecipient();

            // Jeśli odbiorca nie istnieje w bazie, spróbuj znaleźć po email
            if (recipient == null) {
                recipient = userRepository.findByEmail(invitation.getRecipientEmail())
                        .orElse(null);
            }

            if (recipient == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Recipient not found in system"));
            }

            // Utwórz kontakt w obie strony (dwukierunkowy)
            // Kontakt od odbiorcy do nadawcy
            if (!contactRepository.existsByUserIdAndFriendId(recipient.getId(), sender.getId())) {
                Contact contact1 = new Contact();
                contact1.setUser(recipient);
                contact1.setFriend(sender);
                contact1.setFriendName(sender.getUsername());
                contact1.setFriendEmail(sender.getEmail());
                contact1.setStatus("accepted");
                contact1.setCreatedAt(LocalDateTime.now());
                contactRepository.save(contact1);
            }

            // Kontakt od nadawcy do odbiorcy
            if (!contactRepository.existsByUserIdAndFriendId(sender.getId(), recipient.getId())) {
                Contact contact2 = new Contact();
                contact2.setUser(sender);
                contact2.setFriend(recipient);
                contact2.setFriendName(recipient.getUsername());
                contact2.setFriendEmail(recipient.getEmail());
                contact2.setStatus("accepted");
                contact2.setCreatedAt(LocalDateTime.now());
                contactRepository.save(contact2);
            }

            // Zaktualizuj zaproszenie
            invitation.setStatus("accepted");
            invitation.setRespondedAt(LocalDateTime.now());
            invitation.setRecipient(recipient);
            invitation = invitationRepository.save(invitation);

            return ResponseEntity.ok(new InvitationResponseDto(invitation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to accept invitation: " + e.getMessage()));
        }
    }

    /**
     * Odrzuca zaproszenie
     * POST /api/v1/contacts/invitations/{invitationId}/reject
     */
    @PostMapping("/invitations/{invitationId}/reject")
    @Transactional
    public ResponseEntity<?> rejectInvitation(@PathVariable String invitationId) {
        try {
            Long invId = IdMapper.fromInvitationId(invitationId);
            if (invId == null) {
                try {
                    invId = Long.parseLong(invitationId);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid invitation ID format"));
                }
            }

            Invitation invitation = invitationRepository.findById(invId)
                    .orElseThrow(() -> new RuntimeException("Invitation not found"));

            if (!"pending".equals(invitation.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invitation is not pending"));
            }

            invitation.setStatus("rejected");
            invitation.setRespondedAt(LocalDateTime.now());
            
            // Jeśli odbiorca istnieje w bazie, ustaw go
            if (invitation.getRecipient() == null) {
                userRepository.findByEmail(invitation.getRecipientEmail())
                        .ifPresent(invitation::setRecipient);
            }

            invitation = invitationRepository.save(invitation);

            return ResponseEntity.ok(new InvitationResponseDto(invitation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reject invitation: " + e.getMessage()));
        }
    }

    /**
     * Usuwa kontakt
     * DELETE /api/v1/contacts/{contactId}
     */
    @DeleteMapping("/{contactId}")
    @Transactional
    public ResponseEntity<?> removeFriend(@PathVariable String contactId) {
        try {
            Long contactIdLong = IdMapper.fromContactId(contactId);
            if (contactIdLong == null) {
                try {
                    contactIdLong = Long.parseLong(contactId);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid contact ID format"));
                }
            }

            if (contactRepository.existsById(contactIdLong)) {
                contactRepository.deleteById(contactIdLong);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove contact: " + e.getMessage()));
        }
    }

    /**
     * Pobiera listę zaproszeń dla użytkownika
     * GET /api/v1/contacts/invitations/{userId}
     */
    @GetMapping("/invitations/{userId}")
    public ResponseEntity<?> getInvitations(@PathVariable String userId) {
        try {
            Long userIdLong = IdMapper.fromUserId(userId);
            if (userIdLong == null) {
                try {
                    userIdLong = Long.parseLong(userId);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid user ID format"));
                }
            }

            // Pobierz zaproszenia wysłane i otrzymane
            List<Invitation> sentInvitations = invitationRepository.findBySenderId(userIdLong);
            List<Invitation> receivedInvitations = invitationRepository.findByRecipientId(userIdLong);

            List<InvitationResponseDto> allInvitations = sentInvitations.stream()
                    .map(InvitationResponseDto::new)
                    .collect(Collectors.toList());
            
            allInvitations.addAll(receivedInvitations.stream()
                    .map(InvitationResponseDto::new)
                    .toList());

            return ResponseEntity.ok(allInvitations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch invitations: " + e.getMessage()));
        }
    }
}

