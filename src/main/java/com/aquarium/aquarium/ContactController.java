package com.aquarium.aquarium;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping
    public ResponseEntity<?> getContacts() {
        return ResponseEntity.ok(Map.of("error", "JWT authentication not implemented yet"));
    }

    @PostMapping("/invitations")
    public ResponseEntity<?> sendInvitation(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(Map.of("error", "JWT authentication not implemented yet"));
    }

    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<?> acceptInvitation(@PathVariable String invitationId) {
        return ResponseEntity.ok(Map.of("error", "JWT authentication not implemented yet"));
    }

    @PostMapping("/invitations/{invitationId}/reject")
    public ResponseEntity<?> rejectInvitation(@PathVariable String invitationId) {
        return ResponseEntity.ok(Map.of("error", "JWT authentication not implemented yet"));
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<?> removeFriend(@PathVariable String contactId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

