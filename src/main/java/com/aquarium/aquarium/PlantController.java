package com.aquarium.aquarium;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantRepository plantRepository;

    public PlantController(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    @GetMapping
    public List<PlantResponseDto> getAllPlants() {
        return plantRepository.findAll().stream()
                .map(PlantResponseDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlantById(@PathVariable Long id) {
        return plantRepository.findById(id)
                .map(plant -> ResponseEntity.ok(new PlantResponseDto(plant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createPlant(@RequestBody PlantRequestDto request) {
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Name is required"));
            }

            Plant plant = new Plant();
            plant.setName(request.getName());
            plant.setSpecies(request.getSpecies() != null ? request.getSpecies() : request.getName());

            plant = plantRepository.save(plant);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new PlantResponseDto(plant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create plant: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlant(@PathVariable Long id, @RequestBody PlantRequestDto request) {
        try {
            return plantRepository.findById(id)
                    .map(plant -> {
                        if (request.getName() != null && !request.getName().trim().isEmpty()) {
                            plant.setName(request.getName());
                        }
                        if (request.getSpecies() != null) {
                            plant.setSpecies(request.getSpecies());
                        }

                        plant = plantRepository.save(plant);
                        return ResponseEntity.ok(new PlantResponseDto(plant));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update plant: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlant(@PathVariable Long id) {
        try {
            if (plantRepository.existsById(id)) {
                plantRepository.deleteById(id);
                return ResponseEntity.ok(Map.of("message", "Plant deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete plant: " + e.getMessage()));
        }
    }

    public static class PlantRequestDto {
        private String name;
        private String species;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSpecies() {
            return species;
        }

        public void setSpecies(String species) {
            this.species = species;
        }
    }
}
