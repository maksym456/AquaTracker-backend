package com.aquatracker.aquarium;

import com.aquatracker.common.IdMapper;
import com.aquatracker.fish.FishInAquariumDto;
import com.aquatracker.fish.FishSpecies;
import com.aquatracker.fish.FishSpeciesRepository;
import com.aquatracker.plant.Plant;
import com.aquatracker.plant.PlantInAquariumDto;
import com.aquatracker.plant.PlantRepository;
import com.aquatracker.user.User;
import com.aquatracker.user.UserRepository;
import com.aquatracker.logs.LogEntry;
import com.aquatracker.logs.LogEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Profile("!dev")
@RestController
@RequestMapping("/api/v1/aquariums")
public class AquariumController {

    private static final Logger logger = LoggerFactory.getLogger(AquariumController.class);

    private final AquariumRepository aquariumRepository;
    private final FishSpeciesRepository fishRepository;
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final AquariumFishRepository aquariumFishRepository;
    private final AquariumPlantRepository aquariumPlantRepository;
    private final AquariumValidationService validationService;
    private final LogEntryRepository logEntryRepository;

    public AquariumController(AquariumRepository aquariumRepository,
                             FishSpeciesRepository fishRepository,
                             PlantRepository plantRepository,
                             UserRepository userRepository,
                             AquariumFishRepository aquariumFishRepository,
                             AquariumPlantRepository aquariumPlantRepository,
                             AquariumValidationService validationService,
                             LogEntryRepository logEntryRepository) {
        this.aquariumRepository = aquariumRepository;
        this.fishRepository = fishRepository;
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
        this.aquariumFishRepository = aquariumFishRepository;
        this.aquariumPlantRepository = aquariumPlantRepository;
        this.validationService = validationService;
        this.logEntryRepository = logEntryRepository;
    }
    
    private User getOrCreateDefaultUser() {
        return userRepository.findByEmail("default@aquatracker.com")
                .orElseGet(() -> {
                    User defaultUser = new User();
                    defaultUser.setEmail("default@aquatracker.com");
                    defaultUser.setUsername("Default User");
                    defaultUser.setPassword(""); // Hasło nie jest używane przy Cognito
                    defaultUser.setCreatedAt(LocalDateTime.now());
                    return userRepository.save(defaultUser);
                });
    }

    private LogEntry createLogEntry(User user, Aquarium aquarium, String actionType, String title, String message, String metadata) {
        LogEntry logEntry = new LogEntry();
        logEntry.setUser(user);
        logEntry.setAquarium(aquarium);
        logEntry.setAquariumName(aquarium != null ? aquarium.getName() : null);
        logEntry.setActionType(actionType);
        logEntry.setTitle(title);
        logEntry.setMessage(message);
        logEntry.setMetadata(metadata);
        logEntry.setCreatedAt(LocalDateTime.now());
        return logEntryRepository.save(logEntry);
    }

    private Map<String, Object> createLogEntryResponseDto(LogEntry logEntry) {
        Map<String, Object> dto = new java.util.HashMap<>();
        dto.put("id", IdMapper.toLogId(logEntry.getId()));
        dto.put("userId", logEntry.getUser() != null ? IdMapper.toUserId(logEntry.getUser().getId()) : null);
        dto.put("aquariumId", logEntry.getAquarium() != null ? IdMapper.toAquariumId(logEntry.getAquarium().getId()) : null);
        dto.put("aquariumName", logEntry.getAquariumName());
        dto.put("actionType", logEntry.getActionType());
        dto.put("title", logEntry.getTitle());
        dto.put("message", logEntry.getMessage());
        dto.put("createdAt", logEntry.getCreatedAt() != null ? logEntry.getCreatedAt().toString() : null);
        if (logEntry.getMetadata() != null && !logEntry.getMetadata().isEmpty()) {
            try {
                dto.put("metadata", new ObjectMapper().readValue(logEntry.getMetadata(), Map.class));
            } catch (Exception e) {
                dto.put("metadata", Map.of());
            }
        } else {
            dto.put("metadata", Map.of());
        }
        return dto;
    }

    @GetMapping
    public List<AquariumResponseDto> getAllAquariums() {
        return aquariumRepository.findAll().stream()
                .map(aquarium -> new AquariumResponseDto(aquarium, validationService))
                .collect(Collectors.toList());
    }

    /**
     * Pobiera listę akwariów dla użytkownika
     * GET /api/v1/aquariums/user/{userId}
     * 
     * Dla kompatybilności z mock: GET /api/v1/aquariums/{userId} również działa
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAquariumsByUserId(@PathVariable String userId) {
        try {
            Long userIdLong = IdMapper.fromUserId(userId);
            if (userIdLong == null) {
                // Jeśli userId nie jest w formacie u_123, traktuj jako bezpośredni ID (dla kompatybilności z mock)
                try {
                    userIdLong = Long.parseLong(userId);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid user ID format"));
                }
            }

            List<Aquarium> aquariums = aquariumRepository.findByOwnerId(userIdLong);
            List<AquariumResponseDto> aquariumDtos = aquariums.stream()
                    .map(aquarium -> new AquariumResponseDto(aquarium, validationService))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(aquariumDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch aquariums: " + e.getMessage()));
        }
    }

    /**
     * Pobiera akwarium po ID lub listę akwariów dla użytkownika (dla kompatybilności z mock)
     * GET /api/v1/aquariums/{id}
     * 
     * Jeśli id jest w formacie aquarium ID (aq_123) - zwraca pojedyncze akwarium
     * Jeśli id jest userId (liczba lub u_123) - zwraca listę akwariów użytkownika
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAquariumById(@PathVariable String id) {
        // Najpierw sprawdź czy to aquarium ID (format aq_123)
        Long aquariumId = IdMapper.fromAquariumId(id);
        if (aquariumId != null) {
            return aquariumRepository.findById(aquariumId)
                    .map(aquarium -> ResponseEntity.ok(new AquariumResponseDto(aquarium, validationService)))
                    .orElse(ResponseEntity.notFound().build());
        }

        // Jeśli nie jest aquarium ID, sprawdź czy to userId (dla kompatybilności z mock)
        // Mock używa: GET /api/v1/aquariums/{userId}
        Long userIdLong = IdMapper.fromUserId(id);
        if (userIdLong == null) {
            try {
                // Spróbuj jako userId (liczba)
                userIdLong = Long.parseLong(id);
                // Sprawdź czy istnieje użytkownik z tym ID
                if (userRepository.existsById(userIdLong)) {
                    // To jest userId, zwróć akwaria użytkownika
                    List<Aquarium> aquariums = aquariumRepository.findByOwnerId(userIdLong);
                    List<AquariumResponseDto> aquariumDtos = aquariums.stream()
                            .map(aquarium -> new AquariumResponseDto(aquarium, validationService))
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(aquariumDtos);
                }
            } catch (NumberFormatException e) {
                // Nie jest liczbą, zwróć błąd
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID format"));
            }
        } else {
            // To jest userId w formacie u_123
            List<Aquarium> aquariums = aquariumRepository.findByOwnerId(userIdLong);
            List<AquariumResponseDto> aquariumDtos = aquariums.stream()
                    .map(aquarium -> new AquariumResponseDto(aquarium, validationService))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(aquariumDtos);
        }

        return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID format"));
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
            // Mapowanie waterType: frontend może wysłać "freshwater", baza potrzebuje "Słodkowodna"
            String waterType = request.getWaterType();
            if (waterType != null) {
                if (waterType.equals("freshwater")) {
                    waterType = "Słodkowodna";
                } else if (waterType.equals("saltwater")) {
                    waterType = "Słonowodna";
                }
            }
            aquarium.setWaterType(waterType != null ? waterType : "Słodkowodna");
            
            // Mapowanie temperature (frontend może wysłać jako "temperature" lub "temperatureC")
            Double temp = request.getTemperature() != null ? request.getTemperature() : request.getTemperatureC();
            aquarium.setTemperatureC(temp != null ? temp : 24.0);
            
            aquarium.setBiotope(request.getBiotope() != null ? request.getBiotope() : "");
            aquarium.setPh(request.getPh());
            
            // Mapowanie hardness (frontend może wysłać jako "hardness" lub "hardnessDGH")
            Integer hardness = request.getHardness() != null ? request.getHardness() : request.getHardnessDGH();
            aquarium.setHardnessDGH(hardness);
            
            aquarium.setDescription(request.getDescription() != null ? request.getDescription() : "");
            
            // Mapowanie volume (frontend może wysłać jako "volume" lub "volumeLiters")
            Integer volume = request.getVolume() != null ? request.getVolume() : request.getVolumeLiters();
            aquarium.setVolumeLiters(volume != null ? volume : 200);
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
            logger.error("Failed to create aquarium", e);
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
                        // Mapowanie temperature
                        Double temp = request.getTemperature() != null ? request.getTemperature() : request.getTemperatureC();
                        if (temp != null) {
                            aquarium.setTemperatureC(temp);
                        }
                        
                        // Mapowanie waterType
                        String waterType = request.getWaterType();
                        if (waterType != null) {
                            if (waterType.equals("freshwater")) {
                                waterType = "Słodkowodna";
                            } else if (waterType.equals("saltwater")) {
                                waterType = "Słonowodna";
                            }
                            aquarium.setWaterType(waterType);
                        }
                        
                        if (request.getBiotope() != null) {
                            aquarium.setBiotope(request.getBiotope());
                        }
                        if (request.getPh() != null) {
                            aquarium.setPh(request.getPh());
                        }
                        
                        // Mapowanie hardness
                        Integer hardness = request.getHardness() != null ? request.getHardness() : request.getHardnessDGH();
                        if (hardness != null) {
                            aquarium.setHardnessDGH(hardness);
                        }
                        
                        if (request.getDescription() != null) {
                            aquarium.setDescription(request.getDescription());
                        }
                        
                        // Mapowanie volume
                        Integer volume = request.getVolume() != null ? request.getVolume() : request.getVolumeLiters();
                        if (volume != null) {
                            aquarium.setVolumeLiters(volume);
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

            User user = aquarium.getOwner() != null ? aquarium.getOwner() : getOrCreateDefaultUser();
            int count = request.getCount() != null ? request.getCount() : 1;
            
            AquariumFish existingAquariumFish = aquariumFishRepository.findByAquariumIdAndFishSpeciesId(aquariumId, fishId).orElse(null);
            
            if (existingAquariumFish != null) {
                existingAquariumFish.setFishCount(existingAquariumFish.getFishCount() + count);
                aquariumFishRepository.save(existingAquariumFish);
            } else {
                AquariumFish aquariumFish = new AquariumFish();
                aquariumFish.setAquarium(aquarium);
                aquariumFish.setFishSpecies(fishSpecies);
                aquariumFish.setFishCount(count);
                aquariumFishRepository.save(aquariumFish);
            }
            
            aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            AquariumResponseDto response = new AquariumResponseDto(aquarium, validationService);
            
            // Create log entry
            String metadata = String.format("{\"fishId\":\"%s\",\"count\":%d}", request.getFishId(), count);
            LogEntry logEntry = createLogEntry(user, aquarium, "FISH_ADDED", "Dodano ryby", 
                String.format("Dodano %d x %s.", count, fishSpecies.getName()), metadata);
            
            return ResponseEntity.ok(Map.of("aquarium", response, "logEntry", createLogEntryResponseDto(logEntry)));
        } catch (Exception e) {
            logger.error("Failed to add fish to aquarium", e);
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
            User user = aquarium.getOwner() != null ? aquarium.getOwner() : getOrCreateDefaultUser();
            FishSpecies fishSpecies = aquariumFish.getFishSpecies();
            int oldCount = aquariumFish.getFishCount();
            
            if (newCount != null && newCount > 0) {
                aquariumFish.setFishCount(newCount);
                aquariumFishRepository.save(aquariumFish);
            } else if (newCount != null && newCount <= 0) {
                aquariumFishRepository.delete(aquariumFish);
            }
            
            aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            AquariumResponseDto response = new AquariumResponseDto(aquarium, validationService);
            
            // Create log entry
            String metadata = String.format("{\"fishId\":\"%s\",\"count\":%d}", fishId, newCount != null ? newCount : oldCount);
            LogEntry logEntry = createLogEntry(user, aquarium, "FISH_UPDATED", "Zmieniono ilość ryb", 
                String.format("Zmieniono ilość %s na %d.", fishSpecies != null ? fishSpecies.getName() : "ryby", newCount != null ? newCount : oldCount), metadata);
            
            return ResponseEntity.ok(Map.of("aquarium", response, "logEntry", createLogEntryResponseDto(logEntry)));
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
            
            User user = aquarium.getOwner() != null ? aquarium.getOwner() : getOrCreateDefaultUser();
            FishSpecies fishSpecies = aquariumFish.getFishSpecies();
            
            if (count != null && count < aquariumFish.getFishCount()) {
                aquariumFish.setFishCount(aquariumFish.getFishCount() - count);
                aquariumFishRepository.save(aquariumFish);
            } else {
                aquariumFishRepository.delete(aquariumFish);
            }
            
            aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            AquariumResponseDto response = new AquariumResponseDto(aquarium, validationService);
            
            // Create log entry
            String metadata = String.format("{\"fishId\":\"%s\"}", fishId);
            LogEntry logEntry = createLogEntry(user, aquarium, "FISH_REMOVED", "Usunięto ryby", 
                String.format("Usunięto %s.", fishSpecies != null ? fishSpecies.getName() : "ryby"), metadata);
            
            return ResponseEntity.ok(Map.of("aquarium", response, "logEntry", createLogEntryResponseDto(logEntry)));
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

            User user = aquarium.getOwner() != null ? aquarium.getOwner() : getOrCreateDefaultUser();
            int count = request.getCount() != null ? request.getCount() : 1;
            
            AquariumPlant existing = aquariumPlantRepository.findByAquariumIdAndPlantId(aquariumId, plantId).orElse(null);
            if (existing != null) {
                existing.setPlantCount(existing.getPlantCount() + count);
                aquariumPlantRepository.save(existing);
            } else {
                AquariumPlant aquariumPlant = new AquariumPlant();
                aquariumPlant.setAquarium(aquarium);
                aquariumPlant.setPlant(plant);
                aquariumPlant.setPlantCount(count);
                aquariumPlantRepository.save(aquariumPlant);
            }

            aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            AquariumResponseDto response = new AquariumResponseDto(aquarium, validationService);
            
            // Create log entry
            String metadata = String.format("{\"plantId\":\"%s\",\"count\":%d}", request.getPlantId(), count);
            LogEntry logEntry = createLogEntry(user, aquarium, "PLANT_ADDED", "Dodano rośliny", 
                String.format("Dodano %d x %s.", count, plant.getName()), metadata);
            
            return ResponseEntity.ok(Map.of("aquarium", response, "logEntry", createLogEntryResponseDto(logEntry)));
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
            User user = aquarium.getOwner() != null ? aquarium.getOwner() : getOrCreateDefaultUser();
            Plant plant = aquariumPlant.getPlant();
            int oldCount = aquariumPlant.getPlantCount();
            
            if (newCount != null && newCount > 0) {
                aquariumPlant.setPlantCount(newCount);
                aquariumPlantRepository.save(aquariumPlant);
            } else if (newCount != null && newCount <= 0) {
                aquariumPlantRepository.delete(aquariumPlant);
            }
            
            aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            AquariumResponseDto response = new AquariumResponseDto(aquarium, validationService);
            
            // Create log entry
            String metadata = String.format("{\"plantId\":\"%s\",\"count\":%d}", plantId, newCount != null ? newCount : oldCount);
            LogEntry logEntry = createLogEntry(user, aquarium, "PLANT_UPDATED", "Zmieniono ilość roślin", 
                String.format("Zmieniono ilość %s na %d.", plant != null ? plant.getName() : "rośliny", newCount != null ? newCount : oldCount), metadata);
            
            return ResponseEntity.ok(Map.of("aquarium", response, "logEntry", createLogEntryResponseDto(logEntry)));
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
            
            User user = aquarium.getOwner() != null ? aquarium.getOwner() : getOrCreateDefaultUser();
            Plant plant = aquariumPlant.getPlant();
            
            if (count != null && count < aquariumPlant.getPlantCount()) {
                aquariumPlant.setPlantCount(aquariumPlant.getPlantCount() - count);
                aquariumPlantRepository.save(aquariumPlant);
            } else {
                aquariumPlantRepository.delete(aquariumPlant);
            }
            
            aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            AquariumResponseDto response = new AquariumResponseDto(aquarium, validationService);
            
            // Create log entry
            String metadata = String.format("{\"plantId\":\"%s\"}", plantId);
            LogEntry logEntry = createLogEntry(user, aquarium, "PLANT_REMOVED", "Usunięto rośliny", 
                String.format("Usunięto %s.", plant != null ? plant.getName() : "rośliny"), metadata);
            
            return ResponseEntity.ok(Map.of("aquarium", response, "logEntry", createLogEntryResponseDto(logEntry)));
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
        private Double temperature; // frontend format
        private Double temperatureC; // backend format (dla kompatybilności)
        private String biotope;
        private Double ph;
        private Integer hardness; // frontend format
        private Integer hardnessDGH; // backend format (dla kompatybilności)
        private String description;
        private Integer volume; // frontend format
        private Integer volumeLiters; // backend format (dla kompatybilności)
        private List<FishInAquariumDto> fish;
        private List<PlantInAquariumDto> plants;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getWaterType() { return waterType; }
        public void setWaterType(String waterType) { this.waterType = waterType; }
        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }
        public Double getTemperatureC() { return temperatureC; }
        public void setTemperatureC(Double temperatureC) { this.temperatureC = temperatureC; }
        public String getBiotope() { return biotope; }
        public void setBiotope(String biotope) { this.biotope = biotope; }
        public Double getPh() { return ph; }
        public void setPh(Double ph) { this.ph = ph; }
        public Integer getHardness() { return hardness; }
        public void setHardness(Integer hardness) { this.hardness = hardness; }
        public Integer getHardnessDGH() { return hardnessDGH; }
        public void setHardnessDGH(Integer hardnessDGH) { this.hardnessDGH = hardnessDGH; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getVolume() { return volume; }
        public void setVolume(Integer volume) { this.volume = volume; }
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
