package com.aquatracker.fish;

public record FishDto(
        String id,
        String name,
        String species,
        String aquariumId
) {}