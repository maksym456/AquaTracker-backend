package com.aquarium.aquarium;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/plants")
public class PlantController {

    private final PlantRepository plantRepository;

    public PlantController(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    @GetMapping
    public List<PlantResponseDto> getAllPlants(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String biotope,
            @RequestParam(required = false) Integer tempMin,
            @RequestParam(required = false) Integer tempMax,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        return plantRepository.findAll().stream()
                .filter(plant -> {
                    if (q != null && !q.isEmpty()) {
                        if (plant.getName() == null || !plant.getName().toLowerCase().contains(q.toLowerCase())) {
                            return false;
                        }
                    }
                    if (biotope != null && !biotope.isEmpty()) {
                        if (plant.getBiotope() == null || !plant.getBiotope().equalsIgnoreCase(biotope)) {
                            return false;
                        }
                    }
                    if (tempMin != null && plant.getTempMaxC() < tempMin) {
                        return false;
                    }
                    if (tempMax != null && plant.getTempMinC() > tempMax) {
                        return false;
                    }
                    return true;
                })
                .skip(offset != null ? offset : 0)
                .limit(limit != null ? limit : Integer.MAX_VALUE)
                .map(PlantResponseDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlantById(@PathVariable String id) {
        Long plantId = IdMapper.fromPlantId(id);
        if (plantId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid plant ID format"));
        }
        return plantRepository.findById(plantId)
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
    public ResponseEntity<?> updatePlant(@PathVariable String id, @RequestBody PlantRequestDto request) {
        try {
            Long plantId = IdMapper.fromPlantId(id);
            if (plantId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid plant ID format"));
            }
            return plantRepository.findById(plantId)
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
    public ResponseEntity<?> deletePlant(@PathVariable String id) {
        try {
            Long plantId = IdMapper.fromPlantId(id);
            if (plantId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid plant ID format"));
            }
            if (plantRepository.existsById(plantId)) {
                plantRepository.deleteById(plantId);
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
