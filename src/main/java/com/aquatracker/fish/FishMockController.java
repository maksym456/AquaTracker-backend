package com.aquatracker.fish;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Profile("dev")
@RestController
@RequestMapping("/api/v1/fish")
public class FishMockController {

    private static final List<FishDto> FISHES = List.of(
            new FishDto("1", "Welonka (Złota rybka)", "freshwater"),
            new FishDto("2", "Gupik (Głupik)", "freshwater"),
            new FishDto("3", "Neon Innesa", "freshwater"),
            new FishDto("4", "Błazenek pomarańczowy", "saltwater"),
            new FishDto("5", "Pokolec królewski", "saltwater")
    );

    @GetMapping
    public List<FishDto> getAllFishes() {
        return FISHES;
    }

    public record FishDto(String id, String name, String waterType) {}
}
