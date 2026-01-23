package com.aquatracker.aquarium;

import com.aquatracker.common.ErrorResponseDto;
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
import com.aquatracker.history.AquariumParameterHistory;
import com.aquatracker.history.AquariumParameterHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.aquatracker.sharing.AquariumShareRepository;

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
    private final AquariumParameterHistoryRepository parameterHistoryRepository;
    private final AquariumShareRepository aquariumShareRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public AquariumController(AquariumRepository aquariumRepository,
                             FishSpeciesRepository fishRepository,
                             PlantRepository plantRepository,
                             UserRepository userRepository,
                             AquariumFishRepository aquariumFishRepository,
                             AquariumPlantRepository aquariumPlantRepository,
                             AquariumValidationService validationService,
                             LogEntryRepository logEntryRepository,
                             AquariumParameterHistoryRepository parameterHistoryRepository,
                             AquariumShareRepository aquariumShareRepository) {
        this.aquariumRepository = aquariumRepository;
        this.fishRepository = fishRepository;
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
        this.aquariumFishRepository = aquariumFishRepository;
        this.aquariumPlantRepository = aquariumPlantRepository;
        this.validationService = validationService;
        this.logEntryRepository = logEntryRepository;
        this.parameterHistoryRepository = parameterHistoryRepository;
        this.aquariumShareRepository = aquariumShareRepository;
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

    private void saveParameterHistory(Aquarium aquarium, User user, String parameterName, String oldValue, String newValue, String description) {
        if (oldValue != null && newValue != null && !oldValue.equals(newValue)) {
            AquariumParameterHistory history = new AquariumParameterHistory(aquarium, user, parameterName, oldValue, newValue);
            history.setDescription(description);
            parameterHistoryRepository.save(history);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllAquariums(@RequestParam(required = false) String userId) {
        if (userId != null && !userId.trim().isEmpty()) {
            try {
                String userIdString = IdMapper.fromUserId(userId);
                if (userIdString == null) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid user ID format (expected UUID)"));
                }
                
                // Pobierz akwaria, których użytkownik jest właścicielem
                List<Aquarium> ownedAquariums = aquariumRepository.findByOwner_Id(userIdString);
                
                // Pobierz akwaria współdzielone z użytkownikiem
                List<Aquarium> sharedAquariums = aquariumShareRepository.findByUser_Id(userIdString)
                        .stream()
                        .map(share -> share.getAquarium())
                        .filter(aquarium -> aquarium != null)
                        .collect(Collectors.toList());
                
                // Połącz obie listy i usuń duplikaty
                Set<Long> seenIds = new HashSet<>();
                List<Aquarium> allAquariums = new ArrayList<>();
                
                for (Aquarium aq : ownedAquariums) {
                    if (!seenIds.contains(aq.getId())) {
                        allAquariums.add(aq);
                        seenIds.add(aq.getId());
                    }
                }
                
                for (Aquarium aq : sharedAquariums) {
                    if (!seenIds.contains(aq.getId())) {
                        allAquariums.add(aq);
                        seenIds.add(aq.getId());
                    }
                }
                
                List<AquariumResponseDto> aquariumDtos = allAquariums.stream()
                        .map(aquarium -> new AquariumResponseDto(aquarium, validationService))
                        .collect(Collectors.toList());
                return ResponseEntity.ok(aquariumDtos);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to fetch aquariums: " + e.getMessage()));
            }
        }
        return ResponseEntity.ok(List.of());
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
            String userIdString = IdMapper.fromUserId(userId);
            if (userIdString == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID format (expected UUID)"));
            }

            // Pobierz akwaria, których użytkownik jest właścicielem
            List<Aquarium> ownedAquariums = aquariumRepository.findByOwner_Id(userIdString);
            
            // Pobierz akwaria współdzielone z użytkownikiem
            List<Aquarium> sharedAquariums = aquariumShareRepository.findByUser_Id(userIdString)
                    .stream()
                    .map(share -> share.getAquarium())
                    .filter(aquarium -> aquarium != null)
                    .collect(Collectors.toList());
            
            // Połącz obie listy i usuń duplikaty (na wypadek gdyby użytkownik był właścicielem i miał też share)
            Set<Long> seenIds = new HashSet<>();
            List<Aquarium> allAquariums = new ArrayList<>();
            
            for (Aquarium aq : ownedAquariums) {
                if (!seenIds.contains(aq.getId())) {
                    allAquariums.add(aq);
                    seenIds.add(aq.getId());
                }
            }
            
            for (Aquarium aq : sharedAquariums) {
                if (!seenIds.contains(aq.getId())) {
                    allAquariums.add(aq);
                    seenIds.add(aq.getId());
                }
            }
            
            List<AquariumResponseDto> aquariumDtos = allAquariums.stream()
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
    @GetMapping("/{aquariumId}")
    public ResponseEntity<?> getAquariumById(@PathVariable Long aquariumId) {
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
            String waterType = request.getWaterType();
            if (waterType != null) {
                if (waterType.equals("freshwater")) {
                    waterType = "Słodkowodna";
                } else if (waterType.equals("saltwater")) {
                    waterType = "Słonowodna";
                }
            }
            aquarium.setWaterType(waterType != null ? waterType : "Słodkowodna");
            
            Double temp = request.getTemperature() != null ? request.getTemperature() : request.getTemperatureC();
            aquarium.setTemperatureC(temp != null ? temp : 24.0);
            
            aquarium.setBiotope(request.getBiotope() != null ? request.getBiotope() : "");
            aquarium.setPh(request.getPh());
            
            Integer hardness = request.getHardness() != null ? request.getHardness() : request.getHardnessDGH();
            aquarium.setHardnessDGH(hardness);
            
            aquarium.setDescription(request.getDescription() != null ? request.getDescription() : "");
            
            Integer volume = request.getVolume() != null ? request.getVolume() : request.getVolumeLiters();
            aquarium.setVolumeLiters(volume != null ? volume : 200);
            aquarium.setCreatedAt(LocalDateTime.now());
            
            User owner = null;
            if (request.getOwnerId() != null && !request.getOwnerId().trim().isEmpty()) {
                String userIdString = IdMapper.fromUserId(request.getOwnerId());
                if (userIdString != null) {
                    owner = userRepository.findById(userIdString).orElse(null);
                }
            }
            if (owner == null) {
                owner = getOrCreateDefaultUser();
            }
            aquarium.setOwner(owner);

            aquarium = aquariumRepository.save(aquarium);
            
            System.out.println("✓ Aquarium created successfully with ID: " + aquarium.getId());
            System.out.println("  Name: " + aquarium.getName());
            System.out.println("  Owner: " + (aquarium.getOwner() != null ? aquarium.getOwner().getEmail() : "null"));

            User user = aquarium.getOwner();
            LogEntry logEntry = createLogEntry(user, aquarium, "AQUARIUM_CREATED", "Utworzono akwarium", 
                String.format("Akwarium '%s' zostało utworzone.", aquarium.getName()), null);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AquariumResponseDto(aquarium, validationService));
        } catch (Exception e) {
            logger.error("Failed to create aquarium", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create aquarium: " + e.getMessage(), 
                                 "details", e.getClass().getSimpleName()));
        }
    }

    @PutMapping("/{aquariumId}")
    public ResponseEntity<?> updateAquarium(@PathVariable Long aquariumId, @RequestBody AquariumRequestDto request) {
        try {
            return aquariumRepository.findById(aquariumId)
                    .map(aquarium -> {
                        User user = aquarium.getOwner() != null ? aquarium.getOwner() : getOrCreateDefaultUser();
                        
                        if (request.getName() != null && !request.getName().trim().isEmpty()) {
                            String oldName = aquarium.getName();
                            aquarium.setName(request.getName());
                            saveParameterHistory(aquarium, user, "name", oldName, request.getName(), "Zmiana nazwy akwarium");
                        }
                        
                        // Mapowanie waterType
                        String waterType = request.getWaterType();
                        if (waterType != null) {
                            String oldWaterType = aquarium.getWaterType();
                            if (waterType.equals("freshwater")) {
                                waterType = "Słodkowodna";
                            } else if (waterType.equals("saltwater")) {
                                waterType = "Słonowodna";
                            }
                            aquarium.setWaterType(waterType);
                            saveParameterHistory(aquarium, user, "water_type", oldWaterType, waterType, "Zmiana typu wody");
                        }
                        
                        // Mapowanie temperature
                        Double temp = request.getTemperature() != null ? request.getTemperature() : request.getTemperatureC();
                        if (temp != null) {
                            String oldTemp = String.valueOf(aquarium.getTemperatureC());
                            aquarium.setTemperatureC(temp);
                            saveParameterHistory(aquarium, user, "temperature", oldTemp, String.valueOf(temp), "Zmiana temperatury");
                        }
                        
                        if (request.getBiotope() != null) {
                            String oldBiotope = aquarium.getBiotope();
                            aquarium.setBiotope(request.getBiotope());
                            saveParameterHistory(aquarium, user, "biotope", oldBiotope, request.getBiotope(), "Zmiana biotopu");
                        }
                        
                        if (request.getPh() != null) {
                            String oldPh = aquarium.getPh() != null ? String.valueOf(aquarium.getPh()) : null;
                            aquarium.setPh(request.getPh());
                            saveParameterHistory(aquarium, user, "ph", oldPh, String.valueOf(request.getPh()), "Zmiana pH");
                        }
                        
                        // Mapowanie hardness
                        Integer hardness = request.getHardness() != null ? request.getHardness() : request.getHardnessDGH();
                        if (hardness != null) {
                            String oldHardness = aquarium.getHardnessDGH() != null ? String.valueOf(aquarium.getHardnessDGH()) : null;
                            aquarium.setHardnessDGH(hardness);
                            saveParameterHistory(aquarium, user, "hardness", oldHardness, String.valueOf(hardness), "Zmiana twardości wody");
                        }
                        
                        if (request.getDescription() != null) {
                            aquarium.setDescription(request.getDescription());
                        }
                        
                        // Mapowanie volume
                        Integer volume = request.getVolume() != null ? request.getVolume() : request.getVolumeLiters();
                        if (volume != null) {
                            String oldVolume = String.valueOf(aquarium.getVolumeLiters());
                            aquarium.setVolumeLiters(volume);
                            saveParameterHistory(aquarium, user, "volume", oldVolume, String.valueOf(volume), "Zmiana objętości akwarium");
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

    @DeleteMapping("/{aquariumId}")
    @Transactional
    public ResponseEntity<?> deleteAquarium(@PathVariable Long aquariumId) {
        try {
            logger.info("Attempting to delete aquarium with ID: {}", aquariumId);
            
            Aquarium aquarium = aquariumRepository.findById(aquariumId).orElse(null);
            if (aquarium == null) {
                logger.warn("Aquarium with ID {} not found", aquariumId);
                return ResponseEntity.notFound().build();
            }
            
            User user = aquarium.getOwner() != null ? aquarium.getOwner() : getOrCreateDefaultUser();
            String aquariumName = aquarium.getName();
            
            logger.info("Deleting aquarium '{}' (ID: {}). Owner: {}", aquariumName, aquariumId, user.getEmail());
            
            // Set aquarium_id to null in log_entries before deleting aquarium
            // This prevents foreign key constraint violation
            // Używamy bulk update dla lepszej wydajności (szczególnie przy dużej liczbie logów)
            logger.debug("Updating log entries for aquarium {}", aquariumId);
            int logCount = logEntryRepository.findByAquarium_IdOrderByCreatedAtDesc(aquariumId).size();
            logger.debug("Found {} log entries to update", logCount);
            
            if (logCount > 0) {
                logEntryRepository.setAquariumToNullByAquariumId(aquariumId);
                logEntryRepository.flush(); // Wymusza zapis przed dalszymi operacjami
                logger.debug("Updated {} log entries using bulk update", logCount);
            }
            
            // Delete all aquarium shares before deleting aquarium
            // This prevents foreign key constraint violation
            logger.debug("Deleting aquarium shares for aquarium {}", aquariumId);
            aquariumShareRepository.deleteByAquarium_Id(aquariumId);
            aquariumShareRepository.flush(); // Wymusza zapis przed dalszymi operacjami
            logger.debug("Deleted aquarium shares");
            
            // Create log entry for aquarium deletion
            logger.debug("Creating deletion log entry");
            LogEntry logEntry = new LogEntry();
            logEntry.setUser(user);
            logEntry.setAquarium(null); // Set to null since aquarium will be deleted
            logEntry.setAquariumName(aquariumName); // Store name for history
            logEntry.setActionType("AQUARIUM_DELETED");
            logEntry.setTitle("Usunięto akwarium");
            logEntry.setMessage(String.format("Akwarium '%s' zostało usunięte.", aquariumName));
            logEntry.setMetadata(null);
            logEntry.setCreatedAt(LocalDateTime.now());
            logEntryRepository.save(logEntry);
            logEntryRepository.flush();
            logger.debug("Created deletion log entry");
            
            // Now delete the aquarium
            logger.debug("Deleting aquarium entity");
            aquariumRepository.deleteById(aquariumId);
            aquariumRepository.flush();
            logger.info("Successfully deleted aquarium '{}' (ID: {})", aquariumName, aquariumId);
            
            return ResponseEntity.noContent().build(); // 204 No Content zgodnie z OpenAPI
        } catch (Exception e) {
            logger.error("Failed to delete aquarium with ID: {}", aquariumId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("InternalServerError", "Failed to delete aquarium: " + e.getMessage()));
        }
    }

    @PostMapping("/{aquariumId}/fish")
    @Transactional
    public ResponseEntity<?> addFishToAquarium(@PathVariable Long aquariumId, @RequestBody AddFishRequest request) {
        try {
            Long fishId = request.getFishId();
            if (fishId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Fish ID is required"));
            }
            
            Aquarium aquarium = aquariumRepository.findById(aquariumId)
                    .orElseThrow(() -> new RuntimeException("Aquarium not found"));
            
            FishSpecies fishSpecies = fishRepository.findById(fishId)
                    .orElseThrow(() -> new RuntimeException("Fish species not found"));

            User user = aquarium.getOwner() != null ? aquarium.getOwner() : getOrCreateDefaultUser();
            int count = request.getCount() != null ? request.getCount() : 1;
            
            // Zwraca listę, ponieważ mogą być duplikaty w bazie (przed dodaniem unique constraint)
            List<AquariumFish> existingList = aquariumFishRepository.findByAquariumIdAndFishSpeciesId(aquariumId, fishId);
            
            if (!existingList.isEmpty()) {
                // Jeśli są duplikaty, połącz je (usuń wszystkie i utwórz jeden z zsumowanym count)
                int totalCount = existingList.stream().mapToInt(AquariumFish::getFishCount).sum();
                aquariumFishRepository.deleteAll(existingList);
                aquariumFishRepository.flush();
                
                AquariumFish newAquariumFish = new AquariumFish();
                newAquariumFish.setAquarium(aquarium);
                newAquariumFish.setFishSpecies(fishSpecies);
                newAquariumFish.setFishCount(totalCount + count);
                aquariumFishRepository.save(newAquariumFish);
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
            String metadata = String.format("{\"fishId\":%d,\"count\":%d}", request.getFishId(), count);
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

    @PatchMapping("/{aquariumId}/fish/{fishId}")
    @Transactional
    public ResponseEntity<?> updateFishCount(@PathVariable Long aquariumId, @PathVariable Long fishId, @RequestBody Map<String, Integer> request) {
        try {
            Long fId = fishId;
            
            Aquarium aquarium = aquariumRepository.findById(aquariumId)
                    .orElseThrow(() -> new RuntimeException("Aquarium not found"));
            
            List<AquariumFish> aquariumFishList = aquariumFishRepository.findByAquariumIdAndFishSpeciesId(aquariumId, fId);
            if (aquariumFishList.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Fish not found in aquarium"));
            }
            AquariumFish aquariumFish = aquariumFishList.get(0); // Użyj pierwszego
            
            Integer newCount = request.get("count");
            User user = aquarium.getOwner() != null ? aquarium.getOwner() : getOrCreateDefaultUser();
            FishSpecies fishSpecies = aquariumFish.getFishSpecies();
            int oldCount = aquariumFish.getFishCount();
            
            if (newCount != null && newCount > 0) {
                aquariumFish.setFishCount(newCount);
                aquariumFishRepository.save(aquariumFish);
            } else if (newCount != null && newCount <= 0) {
                aquariumFishRepository.delete(aquariumFish);
                aquariumFishRepository.flush(); // Wymusza zapis usunięcia do bazy
                entityManager.refresh(aquarium); // Odświeża obiekt aquarium z bazy
            }
            
            // Pobierz świeży obiekt tylko jeśli nie został odświeżony
            if (newCount == null || newCount > 0) {
                aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            }
            AquariumResponseDto response = new AquariumResponseDto(aquarium, validationService);
            
            // Create log entry
            String metadata = String.format("{\"fishId\":%d,\"count\":%d}", fishId, newCount != null ? newCount : oldCount);
            LogEntry logEntry = createLogEntry(user, aquarium, "FISH_UPDATED", "Zmieniono ilość ryb", 
                String.format("Zmieniono ilość %s na %d.", fishSpecies != null ? fishSpecies.getName() : "ryby", newCount != null ? newCount : oldCount), metadata);
            
            return ResponseEntity.ok(Map.of("aquarium", response, "logEntry", createLogEntryResponseDto(logEntry)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update fish count: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{aquariumId}/fish/{fishId}")
    @Transactional
    public ResponseEntity<?> removeFishFromAquarium(@PathVariable Long aquariumId, @PathVariable Long fishId, @RequestParam(required = false) Integer count) {
        try {
            Long fId = fishId;
            
            Aquarium aquarium = aquariumRepository.findById(aquariumId)
                    .orElseThrow(() -> new RuntimeException("Aquarium not found"));
            
            // Użyj repository zamiast stream() aby uniknąć problemów z lazy loading
            // Zwraca listę, ponieważ mogą być duplikaty w bazie (przed dodaniem unique constraint)
            List<AquariumFish> aquariumFishList = aquariumFishRepository.findByAquariumIdAndFishSpeciesId(aquariumId, fId);
            
            if (aquariumFishList.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Fish not found in aquarium"));
            }
            
            // Jeśli są duplikaty, połącz je (zsumuj count) - ale w rzeczywistości usuniemy wszystkie
            int totalCount = aquariumFishList.stream().mapToInt(AquariumFish::getFishCount).sum();
            FishSpecies fishSpecies = aquariumFishList.get(0).getFishSpecies(); // Użyj pierwszego do pobrania fishSpecies
            
            User user = aquarium.getOwner() != null ? aquarium.getOwner() : getOrCreateDefaultUser();
            
            if (count != null && count < totalCount) {
                // Zmniejsz count - trzeba to zrobić na pierwszym rekordzie i usunąć resztę
                // LUB lepiej: usuń wszystkie duplikaty i utwórz jeden nowy rekord z zaktualizowanym count
                // Na razie uproszczmy: usuń wszystkie i utwórz nowy z zmniejszonym count
                aquariumFishRepository.deleteAll(aquariumFishList);
                aquariumFishRepository.flush();
                
                AquariumFish newAquariumFish = new AquariumFish();
                newAquariumFish.setAquarium(aquarium);
                newAquariumFish.setFishSpecies(fishSpecies);
                newAquariumFish.setFishCount(totalCount - count);
                aquariumFishRepository.save(newAquariumFish);
                aquariumFishRepository.flush();
            } else {
                // Usuń wszystkie rekordy (duplikaty)
                aquariumFishRepository.deleteAll(aquariumFishList);
                aquariumFishRepository.flush(); // Wymusza zapis usunięcia do bazy
            }
            
            entityManager.refresh(aquarium); // Odświeża obiekt aquarium z bazy (aktualizuje kolekcję fishInAquarium)
            AquariumResponseDto response = new AquariumResponseDto(aquarium, validationService);
            
            // Create log entry
            String metadata = String.format("{\"fishId\":%d}", fishId);
            LogEntry logEntry = createLogEntry(user, aquarium, "FISH_REMOVED", "Usunięto ryby", 
                String.format("Usunięto %s.", fishSpecies != null ? fishSpecies.getName() : "ryby"), metadata);
            
            return ResponseEntity.ok(Map.of("aquarium", response, "logEntry", createLogEntryResponseDto(logEntry)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove fish: " + e.getMessage()));
        }
    }

    @PostMapping("/{aquariumId}/plants")
    @Transactional
    public ResponseEntity<?> addPlantToAquarium(@PathVariable Long aquariumId, @RequestBody AddPlantRequest request) {
        try {
            Long plantId = request.getPlantId();
            if (plantId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Plant ID is required"));
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
            String metadata = String.format("{\"plantId\":%d,\"count\":%d}", request.getPlantId(), count);
            LogEntry logEntry = createLogEntry(user, aquarium, "PLANT_ADDED", "Dodano rośliny", 
                String.format("Dodano %d x %s.", count, plant.getName()), metadata);
            
            return ResponseEntity.ok(Map.of("aquarium", response, "logEntry", createLogEntryResponseDto(logEntry)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add plant: " + e.getMessage()));
        }
    }

    @PatchMapping("/{aquariumId}/plants/{plantId}")
    @Transactional
    public ResponseEntity<?> updatePlantCount(@PathVariable Long aquariumId, @PathVariable Long plantId, @RequestBody Map<String, Integer> request) {
        try {
            Long pId = plantId;
            
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
                aquariumPlantRepository.flush(); // Wymusza zapis usunięcia do bazy
                entityManager.refresh(aquarium); // Odświeża obiekt aquarium z bazy
            }
            
            // Pobierz świeży obiekt tylko jeśli nie został odświeżony
            if (newCount == null || newCount > 0) {
                aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            }
            AquariumResponseDto response = new AquariumResponseDto(aquarium, validationService);
            
            // Create log entry
            String metadata = String.format("{\"plantId\":%d,\"count\":%d}", plantId, newCount != null ? newCount : oldCount);
            LogEntry logEntry = createLogEntry(user, aquarium, "PLANT_UPDATED", "Zmieniono ilość roślin", 
                String.format("Zmieniono ilość %s na %d.", plant != null ? plant.getName() : "rośliny", newCount != null ? newCount : oldCount), metadata);
            
            return ResponseEntity.ok(Map.of("aquarium", response, "logEntry", createLogEntryResponseDto(logEntry)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update plant count: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{aquariumId}/plants/{plantId}")
    @Transactional
    public ResponseEntity<?> removePlantFromAquarium(@PathVariable Long aquariumId, @PathVariable Long plantId, @RequestParam(required = false) Integer count) {
        try {
            Long pId = plantId;
            
            Aquarium aquarium = aquariumRepository.findById(aquariumId)
                    .orElseThrow(() -> new RuntimeException("Aquarium not found"));
            
            AquariumPlant aquariumPlant = aquariumPlantRepository.findByAquariumIdAndPlantId(aquariumId, pId)
                    .orElseThrow(() -> new RuntimeException("Plant not found in aquarium"));
            
            User user = aquarium.getOwner() != null ? aquarium.getOwner() : getOrCreateDefaultUser();
            Plant plant = aquariumPlant.getPlant();
            
            if (count != null && count < aquariumPlant.getPlantCount()) {
                aquariumPlant.setPlantCount(aquariumPlant.getPlantCount() - count);
                aquariumPlantRepository.save(aquariumPlant);
                aquarium = aquariumRepository.findById(aquariumId).orElse(aquarium);
            } else {
                aquariumPlantRepository.delete(aquariumPlant);
                aquariumPlantRepository.flush(); // Wymusza zapis usunięcia do bazy
                entityManager.refresh(aquarium); // Odświeża obiekt aquarium z bazy (aktualizuje kolekcję plantsInAquarium)
            }
            AquariumResponseDto response = new AquariumResponseDto(aquarium, validationService);
            
            // Create log entry
            String metadata = String.format("{\"plantId\":%d}", plantId);
            LogEntry logEntry = createLogEntry(user, aquarium, "PLANT_REMOVED", "Usunięto rośliny", 
                String.format("Usunięto %s.", plant != null ? plant.getName() : "rośliny"), metadata);
            
            return ResponseEntity.ok(Map.of("aquarium", response, "logEntry", createLogEntryResponseDto(logEntry)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove plant: " + e.getMessage()));
        }
    }

    @PatchMapping("/{aquariumId}/parameters")
    public ResponseEntity<?> updateAquariumParameters(@PathVariable Long aquariumId, @RequestBody Map<String, Object> params) {
        try {
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

    @PostMapping("/{aquariumId}/status/recalculate")
    public ResponseEntity<?> recalculateStatus(@PathVariable Long aquariumId) {
        try {
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

    @GetMapping("/{aquariumId}/stats")
    public ResponseEntity<?> getAquariumStats(@PathVariable Long aquariumId) {
        return ResponseEntity.ok(Map.of("error", "JWT authentication not implemented yet"));
    }

    public static class AquariumRequestDto {
        private String name;
        private String waterType;
        private Double temperature;
        private Double temperatureC;
        private String biotope;
        private Double ph;
        private Integer hardness;
        private Integer hardnessDGH;
        private String description;
        private Integer volume;
        private Integer volumeLiters;
        private String ownerId;
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
        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
        public List<FishInAquariumDto> getFish() { return fish; }
        public void setFish(List<FishInAquariumDto> fish) { this.fish = fish; }
        public List<PlantInAquariumDto> getPlants() { return plants; }
        public void setPlants(List<PlantInAquariumDto> plants) { this.plants = plants; }
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
        private Integer count;

        public Long getPlantId() { return plantId; }
        public void setPlantId(Long plantId) { this.plantId = plantId; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }
}
