package com.aquatracker.contacts;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Profile("dev")
@RestController
@RequestMapping("/api/v1/contacts")
public class ContactMockController {

    public record ContactDto(String id, String name, String email, String status) {}

    private static final Map<String, List<ContactDto>> CONTACTS_BY_USER = Map.of(
            "f0cc89cc-80e1-7050-3f77-2cf0edd4a8e2", List.of(
                    new ContactDto("1", "Jan Kowalski", "jan.kowalski@example.com", "accepted"),
                    new ContactDto("2", "Anna Nowak", "anna.nowak@example.com", "pending"),
                    new ContactDto("3", "Piotr Wiśniewski", "piotr.wisniewski@example.com", "accepted")
            ),
            "903cd99c-a011-7092-1be5-72afbd7bfafc", List.of(
                    new ContactDto("1", "Katarzyna Zielińska", "k.zielinska@example.com", "accepted"),
                    new ContactDto("2", "Michał Wójcik", "m.wojcik@example.com", "accepted"),
                    new ContactDto("3", "Ola Kaczmarek", "ola.kaczmarek@example.com", "pending")
            ),
            "708cf9dc-a091-70c7-19ef-e1f9875d4a26", List.of(
                    new ContactDto("1", "Tomasz Lewandowski", "t.lewandowski@example.com", "accepted"),
                    new ContactDto("2", "Natalia Dąbrowska", "n.dabrowska@example.com", "pending")
            ),
            "40cc799c-9001-70e2-249e-b23d89e3f1ff", List.of(
                    new ContactDto("1", "Paweł Kamiński", "pawel.kaminski@example.com", "accepted"),
                    new ContactDto("2", "Ewa Szymańska", "ewa.szymanska@example.com", "accepted"),
                    new ContactDto("3", "Łukasz Wróbel", "lukasz.wrobel@example.com", "pending"),
                    new ContactDto("4", "Magda Nowicka", "magda.nowicka@example.com", "accepted")
            )
    );

    // userId == userSub from frontend
    @GetMapping("/{userId}")
    public ResponseEntity<List<ContactDto>> getContacts(@PathVariable String userId) {
        return ResponseEntity.ok(CONTACTS_BY_USER.getOrDefault(userId, List.of()));
    }
}
