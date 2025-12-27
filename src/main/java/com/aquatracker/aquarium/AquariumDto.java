package com.aquatracker.aquarium;

import java.util.List;

public record AquariumDto(
        String id,
        String name,
        int volume,
        List<String> fishes,
        List<String> plants,
        String description
) {}