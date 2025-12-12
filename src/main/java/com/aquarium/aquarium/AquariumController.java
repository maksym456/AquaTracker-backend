package com.aquarium.aquarium;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/aquariums")
public class AquariumController {

    private final AquariumRepository aquariumRepository;
    private final FishSpeciesRepository fishRepository;
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final AquariumFishRepository aquariumFishRepository;
    private final AquariumPlantRepository aquariumPlantRepository;
    private final AquariumValidationService validationService;

    public AquariumController(AquariumRepository aquariumRepository,
                             FishSpeciesRepository fishRepository,
                             PlantRepository plantRepository,
                             UserRepository userRepository,
                             AquariumFishRepository aquariumFishRepository,
                             AquariumPlantRepository aquariumPlantRepository,
                             AquariumValidationService validationService) {
        this.aquariumRepository = aquariumRepository;
        this.fishRepository = fishRepository;
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
        this.aquariumFishRepository = aquariumFishRepository;
        this.aquariumPlantRepository = aquariumPlantRepository;
        this.validationService = validationService;
    }
    
    private User getOrCreateDefaultUser() {
        return userRepository.findByEmail("default@aquatracker.com")
                .orElseGet(() -> {
                    User defaultUser = new User();
                    defaultUser.setEmail("default@aquatracker.com");
                    defaultUser.setUsername("Default User");
                    defaultUser.setPassword("default");
                    defaultUser.setCreatedAt(LocalDateTime.now());
                    return userRepository.save(defaultUser);
                });
    }

    @GetMapping
    public List<AquariumResponseDto> getAllAquariums() {
        return aquariumRepository.findAll().stream()
                .map(aquarium -> new AquariumResponseDto(aquarium, validationService))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAquariumById(@PathVariable String id) {
        Long aquariumId = IdMapper.fromAquariumId(id);
        if (aquariumId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid aquarium ID format"));
        }
        return aquariumRepository.findById(aquariumId)
                .map(aquarium -> ResponseEntity.ok(new AquariumResponseDto(aquarium, validationService)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createAquarium(@RequestBody AquariumRequestDto request) {
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Name is required"));
            }

            Aquarium aquarium = new Aquarium();
            aquarium.setName(request.getName().trim());
            aquarium.setWaterType(request.getWaterType() != null ? request.getWaterType() : "Słodkowodna");
            aquarium.setTemperatureC(request.getTemperatureC() != null ? request.getTemperatureC() : 24.0);
            aquarium.setBiotope(request.getBiotope() != null ? request.getBiotope() : "");
            aquarium.setPh(request.getPh());
            aquarium.setHardnessDGH(request.getHardnessDGH());
            aquarium.setDescription(request.getDescription() != null ? request.getDescription() : "");
            aquarium.setVolumeLiters(request.getVolumeLiters() != null ? request.getVolumeLiters() : 200);
            aquarium.setCreatedAt(LocalDateTime.now());
            // Ustawiamy domyślnego użytkownika, jeśli baza wymaga user_id
            aquarium.setOwner(getOrCreateDefaultUser());
            // Kolekcje są już zainicjalizowane w klasie Aquarium

            aquarium = aquariumRepository.save(aquarium);
            
            System.out.println("✓ Aquarium created successfully with ID: " + aquarium.getId());
            System.out.println("  Name: " + aquarium.getName());
            System.out.println("  Owner: " + (aquarium.getOwner() != null ? aquarium.getOwner().getEmail() : "null"));

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AquariumResponseDto(aquarium, validationService));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create aquarium: " + e.getMessage(), 
                                 "details", e.getClass().getSimpleName()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAquarium(@PathVariable String id, @RequestBody AquariumRequestDto request) {
        try {
            Long aquariumId = IdMapper.fromAquariumId(id);
            if (aquariumId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid aquarium ID format"));
            }
            return aquariumRepository.findById(aquariumId)
                    .map(aquarium -> {
                        if (request.getName() != null && !request.getName().trim().isEmpty()) {
                            aquarium.setName(request.getName());
                        }
                        if (request.getWaterType() != null) {
                            aquarium.setWaterType(request.getWaterType());
                        }
                        if (request.getTemperatureC() != null) {
                            aquarium.setTemperatureC(request.getTemperatureC());
                        }
                        if (request.getBiotope() != null) {
                            aquarium.setBiotope(request.getBiotope());
                        }
                        if (request.getPh() != null) {
                            aquarium.setPh(request.getPh());
                        }
                        if (request.getHardnessDGH() != null) {
                            aquarium.setHardnessDGH(request.getHardnessDGH());
                        }
                        if (request.getDescription() != null) {
                            aquarium.setDescription(request.getDescription());
                        }
                        if (request.getVolumeLiters() != null) {
                            aquarium.setVolumeLiters(request.getVolumeLiters());
                        }

                        aquarium = aquariumRepository.save(aquarium);
                        return ResponseEntity.ok(new AquariumResponseDto(aquarium, validationService));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update aquarium: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAquarium(@PathVariable String id) {
        try {
            Long aquariumId = IdMapper.fromAquariumId(id);
            if (aquariumId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid aquarium ID format"));
            }
            if (aquariumRepository.existsById(aquariumId)) {
                aquariumRepository.deleteById(aquariumId);
                return ResponseEntity.ok(Map.of("message", "Aquarium deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete aquarium: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/fish")
    @Transactional
    public ResponseEntity<?> addFishToAquarium(@PathVariable String id, @RequestBody AddFishRequest request) {
        try {
            Long aquariumId = IdMapper.fromAquariumId(id);
            if (aquariumId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid aquarium ID format"));
            }
            Long fishId = IdMapper.fromFishId(request.getFishId());
            if (fishId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid fish ID format"));
            }
            
            Aquarium aquarium = aquariumRepository.findById(aquariumId)
                    .orElseThrow(() -> new RuntimeException("Aquarium not found"));
            
            FishSpecies fishSpecies = fishRepository.findById(fishId)
                    .orElseThrow(() -> new RuntimeException("Fish species not found"));

            AquariumFish aquariumFish = new AquariumFish();
            aquariumFish.setAquarium(aquarium);
            aquariumFish.setFishSpecies(fishSpecies);
            aquariumFish.setFishCount(request.getCount() != null ? request.getCount() : 1);

            aquariumFish = aquariumFishRepository.save(aquariumFish);
            System.out.println("AquariumFish saved with ID: " + aquariumFish.getId());
            
            aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            AquariumResponseDto response = new AquariumResponseDto(aquarium, validationService);
            return ResponseEntity.ok(Map.of("aquarium", response));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in addFishToAquarium: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add fish: " + e.getMessage(), 
                                 "details", e.getClass().getSimpleName()));
        }
    }

    @PatchMapping("/{id}/fish/{fishId}")
    @Transactional
    public ResponseEntity<?> updateFishCount(@PathVariable String id, @PathVariable String fishId, @RequestBody Map<String, Integer> request) {
        try {
            Long aquariumId = IdMapper.fromAquariumId(id);
            Long fId = IdMapper.fromFishId(fishId);
            if (aquariumId == null || fId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID format"));
            }
            
            Aquarium aquarium = aquariumRepository.findById(aquariumId)
                    .orElseThrow(() -> new RuntimeException("Aquarium not found"));
            
            AquariumFish aquariumFish = aquarium.getFishInAquarium().stream()
                    .filter(af -> af.getFishSpecies() != null && af.getFishSpecies().getId().equals(fId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Fish not found in aquarium"));
            
            Integer newCount = request.get("count");
            if (newCount != null && newCount > 0) {
                aquariumFish.setFishCount(newCount);
                aquariumFishRepository.save(aquariumFish);
            } else if (newCount != null && newCount <= 0) {
                aquariumFishRepository.delete(aquariumFish);
            }
            
            aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            return ResponseEntity.ok(Map.of("aquarium", new AquariumResponseDto(aquarium, validationService)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update fish count: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/fish/{fishId}")
    @Transactional
    public ResponseEntity<?> removeFishFromAquarium(@PathVariable String id, @PathVariable String fishId, @RequestParam(required = false) Integer count) {
        try {
            Long aquariumId = IdMapper.fromAquariumId(id);
            Long fId = IdMapper.fromFishId(fishId);
            if (aquariumId == null || fId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID format"));
            }
            
            Aquarium aquarium = aquariumRepository.findById(aquariumId)
                    .orElseThrow(() -> new RuntimeException("Aquarium not found"));
            
            AquariumFish aquariumFish = aquarium.getFishInAquarium().stream()
                    .filter(af -> af.getFishSpecies() != null && af.getFishSpecies().getId().equals(fId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Fish not found in aquarium"));
            
            if (count != null && count < aquariumFish.getFishCount()) {
                aquariumFish.setFishCount(aquariumFish.getFishCount() - count);
                aquariumFishRepository.save(aquariumFish);
            } else {
                aquariumFishRepository.delete(aquariumFish);
            }
            
            aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            return ResponseEntity.ok(Map.of("aquarium", new AquariumResponseDto(aquarium, validationService)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove fish: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/plants")
    @Transactional
    public ResponseEntity<?> addPlantToAquarium(@PathVariable String id, @RequestBody AddPlantRequest request) {
        try {
            Long aquariumId = IdMapper.fromAquariumId(id);
            if (aquariumId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid aquarium ID format"));
            }
            Long plantId = IdMapper.fromPlantId(request.getPlantId());
            if (plantId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid plant ID format"));
            }
            
            Aquarium aquarium = aquariumRepository.findById(aquariumId)
                    .orElseThrow(() -> new RuntimeException("Aquarium not found"));
            
            Plant plant = plantRepository.findById(plantId)
                    .orElseThrow(() -> new RuntimeException("Plant not found"));

            AquariumPlant existing = aquariumPlantRepository.findByAquariumIdAndPlantId(aquariumId, plantId).orElse(null);
            if (existing != null) {
                existing.setPlantCount(existing.getPlantCount() + (request.getCount() != null ? request.getCount() : 1));
                aquariumPlantRepository.save(existing);
            } else {
                AquariumPlant aquariumPlant = new AquariumPlant();
                aquariumPlant.setAquarium(aquarium);
                aquariumPlant.setPlant(plant);
                aquariumPlant.setPlantCount(request.getCount() != null ? request.getCount() : 1);
                aquariumPlantRepository.save(aquariumPlant);
            }

            aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            return ResponseEntity.ok(Map.of("aquarium", new AquariumResponseDto(aquarium, validationService)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add plant: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/plants/{plantId}")
    @Transactional
    public ResponseEntity<?> updatePlantCount(@PathVariable String id, @PathVariable String plantId, @RequestBody Map<String, Integer> request) {
        try {
            Long aquariumId = IdMapper.fromAquariumId(id);
            Long pId = IdMapper.fromPlantId(plantId);
            if (aquariumId == null || pId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID format"));
            }
            
            Aquarium aquarium = aquariumRepository.findById(aquariumId)
                    .orElseThrow(() -> new RuntimeException("Aquarium not found"));
            
            AquariumPlant aquariumPlant = aquariumPlantRepository.findByAquariumIdAndPlantId(aquariumId, pId)
                    .orElseThrow(() -> new RuntimeException("Plant not found in aquarium"));
            
            Integer newCount = request.get("count");
            if (newCount != null && newCount > 0) {
                aquariumPlant.setPlantCount(newCount);
                aquariumPlantRepository.save(aquariumPlant);
            } else if (newCount != null && newCount <= 0) {
                aquariumPlantRepository.delete(aquariumPlant);
            }
            
            aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            return ResponseEntity.ok(Map.of("aquarium", new AquariumResponseDto(aquarium, validationService)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update plant count: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/plants/{plantId}")
    @Transactional
    public ResponseEntity<?> removePlantFromAquarium(@PathVariable String id, @PathVariable String plantId, @RequestParam(required = false) Integer count) {
        try {
            Long aquariumId = IdMapper.fromAquariumId(id);
            Long pId = IdMapper.fromPlantId(plantId);
            if (aquariumId == null || pId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID format"));
            }
            
            Aquarium aquarium = aquariumRepository.findById(aquariumId)
                    .orElseThrow(() -> new RuntimeException("Aquarium not found"));
            
            AquariumPlant aquariumPlant = aquariumPlantRepository.findByAquariumIdAndPlantId(aquariumId, pId)
                    .orElseThrow(() -> new RuntimeException("Plant not found in aquarium"));
            
            if (count != null && count < aquariumPlant.getPlantCount()) {
                aquariumPlant.setPlantCount(aquariumPlant.getPlantCount() - count);
                aquariumPlantRepository.save(aquariumPlant);
            } else {
                aquariumPlantRepository.delete(aquariumPlant);
            }
            
            aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            return ResponseEntity.ok(Map.of("aquarium", new AquariumResponseDto(aquarium, validationService)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove plant: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/parameters")
    public ResponseEntity<?> updateAquariumParameters(@PathVariable String id, @RequestBody Map<String, Object> params) {
        try {
            Long aquariumId = IdMapper.fromAquariumId(id);
            if (aquariumId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid aquarium ID format"));
            }
            return aquariumRepository.findById(aquariumId)
                    .map(aquarium -> {
                        if (params.containsKey("temperatureC")) {
                            aquarium.setTemperatureC(((Number) params.get("temperatureC")).doubleValue());
                        }
                        if (params.containsKey("ph")) {
                            aquarium.setPh(((Number) params.get("ph")).doubleValue());
                        }
                        if (params.containsKey("hardnessDGH")) {
                            aquarium.setHardnessDGH(((Number) params.get("hardnessDGH")).intValue());
                        }
                        aquarium = aquariumRepository.save(aquarium);
                        return ResponseEntity.ok(new AquariumResponseDto(aquarium, validationService));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update parameters: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/status/recalculate")
    public ResponseEntity<?> recalculateStatus(@PathVariable String id) {
        try {
            Long aquariumId = IdMapper.fromAquariumId(id);
            if (aquariumId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid aquarium ID format"));
            }
            return aquariumRepository.findById(aquariumId)
                    .map(aquarium -> {
                        AquariumStatusDto status = validationService.validateAquarium(aquarium);
                        return ResponseEntity.ok(Map.of("status", status));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to recalculate status: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getGlobalStats() {
        return ResponseEntity.ok(Map.of("error", "JWT authentication not implemented yet"));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getAquariumStats(@PathVariable String id) {
        return ResponseEntity.ok(Map.of("error", "JWT authentication not implemented yet"));
    }

    public static class AquariumRequestDto {
        private String name;
        private String waterType;
        private Double temperatureC;
        private String biotope;
        private Double ph;
        private Integer hardnessDGH;
        private String description;
        private Integer volumeLiters;
        private List<FishInAquariumDto> fish;
        private List<PlantInAquariumDto> plants;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getWaterType() { return waterType; }
        public void setWaterType(String waterType) { this.waterType = waterType; }
        public Double getTemperatureC() { return temperatureC; }
        public void setTemperatureC(Double temperatureC) { this.temperatureC = temperatureC; }
        public String getBiotope() { return biotope; }
        public void setBiotope(String biotope) { this.biotope = biotope; }
        public Double getPh() { return ph; }
        public void setPh(Double ph) { this.ph = ph; }
        public Integer getHardnessDGH() { return hardnessDGH; }
        public void setHardnessDGH(Integer hardnessDGH) { this.hardnessDGH = hardnessDGH; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getVolumeLiters() { return volumeLiters; }
        public void setVolumeLiters(Integer volumeLiters) { this.volumeLiters = volumeLiters; }
        public List<FishInAquariumDto> getFish() { return fish; }
        public void setFish(List<FishInAquariumDto> fish) { this.fish = fish; }
        public List<PlantInAquariumDto> getPlants() { return plants; }
        public void setPlants(List<PlantInAquariumDto> plants) { this.plants = plants; }
    }

    public static class AddFishRequest {
        private String fishId;
        private Integer count;

        public String getFishId() { return fishId; }
        public void setFishId(String fishId) { this.fishId = fishId; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }

    public static class AddPlantRequest {
        private String plantId;
        private Integer count;

        public String getPlantId() { return plantId; }
        public void setPlantId(String plantId) { this.plantId = plantId; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }
}
