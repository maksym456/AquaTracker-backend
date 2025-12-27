package com.aquatracker;

import com.aquatracker.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/settings")
public class SettingsController {

    private final UserRepository userRepository;

    public SettingsController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getSettings() {
        return ResponseEntity.ok(Map.of("error", "JWT authentication not implemented yet"));
    }

    @PatchMapping
    public ResponseEntity<?> updateSettings(@RequestBody Map<String, Object> settings) {
        return ResponseEntity.ok(Map.of("error", "JWT authentication not implemented yet"));
    }
}

