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
@RequestMapping("/api/aquariums")
public class AquariumController {

    private final AquariumRepository aquariumRepository;
    private final FishSpeciesRepository fishRepository;
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final AquariumFishRepository aquariumFishRepository;

    public AquariumController(AquariumRepository aquariumRepository,
                             FishSpeciesRepository fishRepository,
                             PlantRepository plantRepository,
                             UserRepository userRepository,
                             AquariumFishRepository aquariumFishRepository) {
        this.aquariumRepository = aquariumRepository;
        this.fishRepository = fishRepository;
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
        this.aquariumFishRepository = aquariumFishRepository;
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
                .map(AquariumResponseDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAquariumById(@PathVariable Long id) {
        return aquariumRepository.findById(id)
                .map(aquarium -> ResponseEntity.ok(new AquariumResponseDto(aquarium)))
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
            aquarium.setWaterType(request.getWaterType() != null ? request.getWaterType() : "freshwater");
            aquarium.setTemperatureC(request.getTemperature() != null ? request.getTemperature() : 24.0);
            aquarium.setBiotope(request.getBiotope() != null ? request.getBiotope() : "");
            aquarium.setPh(request.getPh());
            aquarium.setHardness(request.getHardness());
            aquarium.setDescription(request.getDescription() != null ? request.getDescription() : "");
            aquarium.setVolumeLiters(request.getVolume() != null ? request.getVolume() : 200);
            aquarium.setCreatedAt(LocalDateTime.now());
            aquarium.setOwner(getOrCreateDefaultUser());

            aquarium = aquariumRepository.save(aquarium);
            
            System.out.println("✓ Aquarium created successfully with ID: " + aquarium.getId());
            System.out.println("  Name: " + aquarium.getName());
            System.out.println("  Owner: " + (aquarium.getOwner() != null ? aquarium.getOwner().getEmail() : "null"));

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AquariumResponseDto(aquarium));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create aquarium: " + e.getMessage(), 
                                 "details", e.getClass().getSimpleName()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAquarium(@PathVariable Long id, @RequestBody AquariumRequestDto request) {
        try {
            return aquariumRepository.findById(id)
                    .map(aquarium -> {
                        if (request.getName() != null && !request.getName().trim().isEmpty()) {
                            aquarium.setName(request.getName());
                        }
                        if (request.getWaterType() != null) {
                            aquarium.setWaterType(request.getWaterType());
                        }
                        if (request.getTemperature() != null) {
                            aquarium.setTemperatureC(request.getTemperature());
                        }
                        if (request.getBiotope() != null) {
                            aquarium.setBiotope(request.getBiotope());
                        }
                        if (request.getPh() != null) {
                            aquarium.setPh(request.getPh());
                        }
                        if (request.getHardness() != null) {
                            aquarium.setHardness(request.getHardness());
                        }
                        if (request.getDescription() != null) {
                            aquarium.setDescription(request.getDescription());
                        }
                        if (request.getVolume() != null) {
                            aquarium.setVolumeLiters(request.getVolume());
                        }

                        aquarium = aquariumRepository.save(aquarium);
                        return ResponseEntity.ok(new AquariumResponseDto(aquarium));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update aquarium: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAquarium(@PathVariable Long id) {
        try {
            if (aquariumRepository.existsById(id)) {
                aquariumRepository.deleteById(id);
                return ResponseEntity.ok(Map.of("message", "Aquarium deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete aquarium: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/fishes")
    public ResponseEntity<?> addFishToAquarium(@PathVariable Long id, @RequestBody AddFishRequest request) {
        try {
            Aquarium aquarium = aquariumRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Aquarium not found"));
            
            FishSpecies fishSpecies = fishRepository.findById(request.getFishId())
                    .orElseThrow(() -> new RuntimeException("Fish species not found"));

            AquariumFish aquariumFish = new AquariumFish();
            aquariumFish.setAquarium(aquarium);
            aquariumFish.setFishSpecies(fishSpecies);
            aquariumFish.setFishCount(request.getCount() != null ? request.getCount() : 1);

            aquariumFish = aquariumFishRepository.save(aquariumFish);
            aquarium = aquariumRepository.findById(id).orElse(aquarium);

            return ResponseEntity.ok(new AquariumResponseDto(aquarium));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add fish: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/plants")
    public ResponseEntity<?> addPlantToAquarium(@PathVariable Long id, @RequestBody AddPlantRequest request) {
        try {
            Aquarium aquarium = aquariumRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Aquarium not found"));
            
            Plant plant = plantRepository.findById(request.getPlantId())
                    .orElseThrow(() -> new RuntimeException("Plant not found"));

            aquarium.getPlants().add(plant);
            aquariumRepository.save(aquarium);

            return ResponseEntity.ok(new AquariumResponseDto(aquarium));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add plant: " + e.getMessage()));
        }
    }

    public static class AquariumRequestDto {
        private String name;
        private String waterType;
        private Double temperature;
        private String biotope;
        private Double ph;
        private Integer hardness;
        private String description;
        private Integer volume;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getWaterType() { return waterType; }
        public void setWaterType(String waterType) { this.waterType = waterType; }
        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }
        public String getBiotope() { return biotope; }
        public void setBiotope(String biotope) { this.biotope = biotope; }
        public Double getPh() { return ph; }
        public void setPh(Double ph) { this.ph = ph; }
        public Integer getHardness() { return hardness; }
        public void setHardness(Integer hardness) { this.hardness = hardness; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getVolume() { return volume; }
        public void setVolume(Integer volume) { this.volume = volume; }
    }

    public static class AddFishRequest {
        private Long fishId;
        private Integer count;

        public Long getFishId() { return fishId; }
        public void setFishId(Long fishId) { this.fishId = fishId; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }

    public static class AddPlantRequest {
        private Long plantId;

        public Long getPlantId() { return plantId; }
        public void setPlantId(Long plantId) { this.plantId = plantId; }
    }
}
