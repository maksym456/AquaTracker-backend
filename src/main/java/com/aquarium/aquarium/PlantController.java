package com.aquarium.aquarium;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    @GetMapping
    public List<PlantDto> getAllPlants() {
        return List.of(
                new PlantDto("1", "Anubias", "1"),
                new PlantDto("2", "Moczarka", "2")
        );
    }
}