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
            new FishDto(
                    "1",
                    "Welonka (Złota rybka)",
                    "Słodkowodna",
                    "18-22",
                    "7.0-8.0",
                    "5-19",
                    "temperate",
                    "spokojne",
                    1,
                    "10-15 lat"
            ),
            new FishDto(
                    "2",
                    "Gupik (Głupik)",
                    "Słodkowodna",
                    "22-28",
                    "6.8-7.8",
                    "8-20",
                    "tropical",
                    "spokojne",
                    6,
                    "2-3 lata"
            ),
            new FishDto(
                    "3",
                    "Neon Innesa",
                    "Słodkowodna",
                    "20-26",
                    "5.0-7.0",
                    "1-10",
                    "amazon",
                    "spokojne",
                    10,
                    "3-5 lat"
            ),
            new FishDto(
                    "4",
                    "Błazenek pomarańczowy",
                    "Słonowodna",
                    "24-27",
                    "8.1-8.4",
                    "8-12",
                    "coralReef",
                    "pół-agresywne",
                    2,
                    "6-10 lat"
            ),
            new FishDto(
                    "5",
                    "Pokolec królewski",
                    "Słonowodna",
                    "24-28",
                    "8.1-8.4",
                    "8-12",
                    "coralReef",
                    "umiarkowane",
                    1,
                    "8-12 lat"
            )
    );

    @GetMapping
    public List<FishDto> getAllFishes() {
        return FISHES;
    }

    public record FishDto(
            String id,
            String name,
            String waterType,
            String temperature,
            String ph,
            String hardnessDGH,
            String biotope,
            String temperament,
            Integer minShoalSize,
            String lifeSpan
    ) {}
}