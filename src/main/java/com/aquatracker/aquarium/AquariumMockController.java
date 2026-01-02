package com.aquatracker.aquarium;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Profile("dev")
@RestController
@RequestMapping("/api/v1/aquariums")
public class AquariumMockController {

    private static final String U1 = "f0cc89cc-80e1-7050-3f77-2cf0edd4a8e2";
    private static final String U2 = "903cd99c-a011-7092-1be5-72afbd7bfafc";
    private static final String U3 = "708cf9dc-a091-70c7-19ef-e1f9875d4a26";
    private static final String U4 = "40cc799c-9001-70e2-249e-b23d89e3f1ff";

    private static final Map<String, List<Map<String, Object>>> MOCK = Map.of(
            U1, List.of(
                    Map.of(
                            "id", "aq_u1_1",
                            "name", "Amazon Community",
                            "description", "Blackwater-style setup with plants and small schooling fish.",
                            "temperature", 25.5,
                            "ph", 6.6,
                            "hardness", 5,
                            "fishes", List.of(
                                    Map.of("id", "fish_1", "species", "Neon Tetra", "count", 12),
                                    Map.of("id", "fish_2", "species", "Corydoras", "count", 6),
                                    Map.of("id", "fish_3", "species", "Angelfish", "count", 2)
                            ),
                            "plants", List.of(
                                    Map.of("id", "plant_1", "name", "Amazon Sword", "count", 3),
                                    Map.of("id", "plant_2", "name", "Anubias", "count", 2),
                                    Map.of("id", "plant_3", "name", "Java Fern", "count", 2)
                            )
                    ),
                    Map.of(
                            "id", "aq_u1_2",
                            "name", "Nano Shrimp",
                            "description", "Low-tech nano with shrimp and moss.",
                            "temperature", 23.0,
                            "ph", 7.2,
                            "hardness", 8,
                            "fishes", List.of(
                                    Map.of("id", "fish_4", "species", "Otocinclus", "count", 3)
                            ),
                            "plants", List.of(
                                    Map.of("id", "plant_4", "name", "Java Moss", "count", 1),
                                    Map.of("id", "plant_5", "name", "Cryptocoryne", "count", 4)
                            )
                    )
            ),

            U2, List.of(
                    Map.of(
                            "id", "aq_u2_1",
                            "name", "Guppy Breeder",
                            "description", "Harder water, livebearer-focused.",
                            "temperature", 24.0,
                            "ph", 7.6,
                            "hardness", 14,
                            "fishes", List.of(
                                    Map.of("id", "fish_10", "species", "Guppy", "count", 18),
                                    Map.of("id", "fish_11", "species", "Platy", "count", 8)
                            ),
                            "plants", List.of(
                                    Map.of("id", "plant_10", "name", "Hornwort", "count", 3),
                                    Map.of("id", "plant_11", "name", "Anacharis", "count", 4)
                            )
                    )
            ),

            U3, List.of(
                    Map.of(
                            "id", "aq_u3_1",
                            "name", "Malawi Cichlids",
                            "description", "Rockscape and territorial cichlids.",
                            "temperature", 26.0,
                            "ph", 8.0,
                            "hardness", 18,
                            "fishes", List.of(
                                    Map.of("id", "fish_20", "species", "Yellow Lab (Labidochromis caeruleus)", "count", 6),
                                    Map.of("id", "fish_21", "species", "Zebra Mbuna", "count", 5)
                            ),
                            "plants", List.of(
                                    Map.of("id", "plant_20", "name", "Anubias", "count", 1)
                            )
                    ),
                    Map.of(
                            "id", "aq_u3_2",
                            "name", "Planted Iwagumi",
                            "description", "CO2-style layout (mock).",
                            "temperature", 24.5,
                            "ph", 6.8,
                            "hardness", 6,
                            "fishes", List.of(
                                    Map.of("id", "fish_22", "species", "Amano Shrimp", "count", 8),
                                    Map.of("id", "fish_23", "species", "Rasbora", "count", 10)
                            ),
                            "plants", List.of(
                                    Map.of("id", "plant_21", "name", "Dwarf Hairgrass", "count", 12),
                                    Map.of("id", "plant_22", "name", "Monte Carlo", "count", 6)
                            )
                    )
            ),

            U4, List.of(
                    Map.of(
                            "id", "aq_u4_1",
                            "name", "Reef Starter",
                            "description", "Saltwater mock with fish and macroalgae.",
                            "temperature", 26.0,
                            "ph", 8.1,
                            "hardness", 10,
                            "fishes", List.of(
                                    Map.of("id", "fish_30", "species", "Ocellaris Clownfish", "count", 2),
                                    Map.of("id", "fish_31", "species", "Royal Gramma", "count", 1)
                            ),
                            "plants", List.of(
                                    Map.of("id", "plant_30", "name", "Chaetomorpha (macroalgae)", "count", 1),
                                    Map.of("id", "plant_31", "name", "Caulerpa (macroalgae)", "count", 1)
                            )
                    )
            )
    );

    @GetMapping("/{userId}")
    public ResponseEntity<?> getAquariums(@PathVariable String userId) {
        return ResponseEntity.ok(MOCK.getOrDefault(userId, List.of()));
    }
}
