package com.aquatracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SimpleApiController {

    private static final String API_TOKEN = "supersekretnytoken";

    @GetMapping("/fish")
    public ResponseEntity<?> fish(
            @RequestHeader(value = "X-API-TOKEN", required = false) String token
    ) {
        if (!API_TOKEN.equals(token)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Brak poprawnego tokenu API"));
        }

        List<String> fishNames = List.of(
                "Gupik",
                "Złota rybka",
                "Molinezja",
                "Neon Innesa"
        );

        return ResponseEntity.ok(Map.of("rybki", fishNames));
    }
}
