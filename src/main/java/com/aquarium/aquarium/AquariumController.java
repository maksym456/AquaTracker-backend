package com.aquarium.aquarium;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/aquariums")
public class AquariumController {

    @GetMapping
    public List<AquariumDto> getAllAquariums() {
        return List.of(
                new AquariumDto("1", "Moje pierwsze akwarium", 200, List.of("1"), List.of("1", "2"), "Pierwsze domowe akwarium"),
                new AquariumDto("2", "Drugie akwarium", 200, List.of("2"), List.of("3"), "Drugie domowe akwarium")
        );
    }
}