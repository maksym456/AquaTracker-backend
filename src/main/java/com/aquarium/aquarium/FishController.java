package com.aquarium.aquarium;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/fish")
public class FishController {

    private final FishSpeciesRepository fishRepository;

    public FishController(FishSpeciesRepository fishRepository) {
        this.fishRepository = fishRepository;
    }

    @GetMapping
    public List<FishResponseDto> getAllFishes() {
        return fishRepository.findAll().stream()
                .map(FishResponseDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFishById(@PathVariable String id) {
        Long fishId = IdMapper.fromFishId(id);
        if (fishId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid fish ID format"));
        }
        return fishRepository.findById(fishId)
                .map(fish -> ResponseEntity.ok(new FishResponseDto(fish)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<FishResponseDto> searchFishes(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String waterType,
            @RequestParam(required = false) String temperament,
            @RequestParam(required = false) String biotope,
            @RequestParam(required = false) Integer tempMin,
            @RequestParam(required = false) Integer tempMax,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        
        List<FishSpecies> allFishes = fishRepository.findAll();
        
        return allFishes.stream()
                .filter(fish -> {
                    if (q != null && !q.isEmpty()) {
                        if (fish.getName() == null || !fish.getName().toLowerCase().contains(q.toLowerCase())) {
                            return false;
                        }
                    }
                    if (waterType != null && !waterType.isEmpty()) {
                        if (fish.getWaterType() == null || !fish.getWaterType().equals(waterType)) {
                            return false;
                        }
                    }
                    if (temperament != null && !temperament.isEmpty()) {
                        if (!temperament.equals(fish.getTemperament())) {
                            return false;
                        }
                    }
                    if (biotope != null && !biotope.isEmpty()) {
                        if (fish.getBiotype() == null || !fish.getBiotype().equalsIgnoreCase(biotope)) {
                            return false;
                        }
                    }
                    if (tempMin != null && fish.getTempMaxC() < tempMin) {
                        return false;
                    }
                    if (tempMax != null && fish.getTempMinC() > tempMax) {
                        return false;
                    }
                    return true;
                })
                .skip(offset != null ? offset : 0)
                .limit(limit != null ? limit : Integer.MAX_VALUE)
                .map(FishResponseDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> createFish(@RequestBody FishRequestDto request) {
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Name is required"));
            }

            String waterType = "Słodkowodna";
            if (request.getWaterType() != null && request.getWaterType().equals("saltwater")) {
                waterType = "Słonowodna";
            }

            FishSpecies fish = new FishSpecies();
            fish.setName(request.getName());
            fish.setDescription(request.getDescription() != null ? request.getDescription() : "");
            fish.setImage(request.getImage() != null ? request.getImage() : "");
            fish.setWaterType(waterType);
            fish.setTempMinC(request.getTempRange() != null && !request.getTempRange().isEmpty() 
                ? request.getTempRange().get(0) : 22);
            fish.setTempMaxC(request.getTempRange() != null && request.getTempRange().size() > 1 
                ? request.getTempRange().get(1) : 26);
            fish.setBiotype(request.getBiotope() != null ? request.getBiotope() : "");
            fish.setPhMin(request.getPhRange() != null && !request.getPhRange().isEmpty() 
                ? request.getPhRange().get(0) : 6.5);
            fish.setPhMax(request.getPhRange() != null && request.getPhRange().size() > 1 
                ? request.getPhRange().get(1) : 7.5);
            fish.setGhMin(request.getHardness() != null && !request.getHardness().isEmpty() 
                ? request.getHardness().get(0) : 5);
            fish.setGhMax(request.getHardness() != null && request.getHardness().size() > 1 
                ? request.getHardness().get(1) : 15);
            fish.setTemperament(request.getTemperament() != null ? request.getTemperament() : "spokojne");
            fish.setMinSchoolSize(request.getMinSchoolSize() != null ? request.getMinSchoolSize() : 1);
            fish.setLifespan(request.getLifespan() != null ? request.getLifespan() : "3-5 lat");

            fish = fishRepository.save(fish);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new FishResponseDto(fish));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create fish: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFish(@PathVariable String id, @RequestBody FishRequestDto request) {
        try {
            Long fishId = IdMapper.fromFishId(id);
            if (fishId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid fish ID format"));
            }
            return fishRepository.findById(fishId)
                    .map(fish -> {
                        if (request.getName() != null && !request.getName().trim().isEmpty()) {
                            fish.setName(request.getName());
                        }
                        if (request.getDescription() != null) {
                            fish.setDescription(request.getDescription());
                        }
                        if (request.getImage() != null) {
                            fish.setImage(request.getImage());
                        }
                        if (request.getWaterType() != null) {
                            String waterType = request.getWaterType().equals("saltwater") ? "Słonowodna" : "Słodkowodna";
                            fish.setWaterType(waterType);
                        }
                        if (request.getTempRange() != null && !request.getTempRange().isEmpty()) {
                            fish.setTempMinC(request.getTempRange().get(0));
                            if (request.getTempRange().size() > 1) {
                                fish.setTempMaxC(request.getTempRange().get(1));
                            }
                        }
                        if (request.getBiotope() != null) {
                            fish.setBiotype(request.getBiotope());
                        }
                        if (request.getPhRange() != null && !request.getPhRange().isEmpty()) {
                            fish.setPhMin(request.getPhRange().get(0));
                            if (request.getPhRange().size() > 1) {
                                fish.setPhMax(request.getPhRange().get(1));
                            }
                        }
                        if (request.getHardness() != null && !request.getHardness().isEmpty()) {
                            fish.setGhMin(request.getHardness().get(0));
                            if (request.getHardness().size() > 1) {
                                fish.setGhMax(request.getHardness().get(1));
                            }
                        }
                        if (request.getTemperament() != null) {
                            fish.setTemperament(request.getTemperament());
                        }
                        if (request.getMinSchoolSize() != null) {
                            fish.setMinSchoolSize(request.getMinSchoolSize());
                        }
                        if (request.getLifespan() != null) {
                            fish.setLifespan(request.getLifespan());
                        }

                        fish = fishRepository.save(fish);
                        return ResponseEntity.ok(new FishResponseDto(fish));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update fish: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFish(@PathVariable String id) {
        try {
            Long fishId = IdMapper.fromFishId(id);
            if (fishId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid fish ID format"));
            }
            if (fishRepository.existsById(fishId)) {
                fishRepository.deleteById(fishId);
                return ResponseEntity.ok(Map.of("message", "Fish deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete fish: " + e.getMessage()));
        }
    }
}
