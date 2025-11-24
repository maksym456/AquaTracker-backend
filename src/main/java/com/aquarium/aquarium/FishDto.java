package com.aquarium.aquarium;

public record FishDto(
        String id,
        String name,
        String species,
        String aquariumId
) {}