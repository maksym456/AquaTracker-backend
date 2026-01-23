package com.aquatracker.admin;

import com.aquatracker.aquarium.Aquarium;
import com.aquatracker.aquarium.AquariumFish;
import com.aquatracker.aquarium.AquariumPlant;
import com.aquatracker.aquarium.AquariumRepository;
import com.aquatracker.aquarium.AquariumFishRepository;
import com.aquatracker.aquarium.AquariumPlantRepository;
import com.aquatracker.common.IdMapper;
import com.aquatracker.logs.LogEntry;
import com.aquatracker.logs.LogEntryRepository;
import com.aquatracker.user.User;
import com.aquatracker.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final AquariumRepository aquariumRepository;
    private final AquariumFishRepository aquariumFishRepository;
    private final AquariumPlantRepository aquariumPlantRepository;
    private final LogEntryRepository logEntryRepository;

    public AdminController(
            UserRepository userRepository,
            AquariumRepository aquariumRepository,
            AquariumFishRepository aquariumFishRepository,
            AquariumPlantRepository aquariumPlantRepository,
            LogEntryRepository logEntryRepository) {
        this.userRepository = userRepository;
        this.aquariumRepository = aquariumRepository;
        this.aquariumFishRepository = aquariumFishRepository;
        this.aquariumPlantRepository = aquariumPlantRepository;
        this.logEntryRepository = logEntryRepository;
    }

    /**
     * Sprawdza czy użytkownik ma uprawnienia administratora
     * GET /api/admin/check-access
     * 
     * TODO: Po implementacji pełnej integracji z AWS Cognito, sprawdzić grupę "admin"
     * Na razie sprawdzamy pole isAdmin w bazie danych
     */
    @GetMapping("/check-access")
    public ResponseEntity<?> checkAdminAccess(@RequestParam(required = false) String cognitoSub) {
        try {
            if (cognitoSub == null || cognitoSub.trim().isEmpty()) {
                return ResponseEntity.ok(Map.of("isAdmin", false));
            }

            Optional<User> userOpt = userRepository.findByCognitoSub(cognitoSub.trim());
            if (userOpt.isEmpty()) {
                return ResponseEntity.ok(Map.of("isAdmin", false));
            }

            User user = userOpt.get();
            boolean isAdmin = user.getIsAdmin() != null && user.getIsAdmin();
            
            return ResponseEntity.ok(Map.of("isAdmin", isAdmin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check admin access: " + e.getMessage()));
        }
    }

    /**
     * Pobiera listę użytkowników z filtrowaniem i paginacją
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int limit) {
        try {
            List<User> allUsers = userRepository.findAll();
            
            // Filtrowanie po search (email, username, ID)
            if (search != null && !search.trim().isEmpty()) {
                String searchLower = search.trim().toLowerCase();
                allUsers = allUsers.stream()
                        .filter(user -> 
                            (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchLower)) ||
                            (user.getUsername() != null && user.getUsername().toLowerCase().contains(searchLower)) ||
                            (user.getId() != null && user.getId().toLowerCase().contains(searchLower))
                        )
                        .collect(Collectors.toList());
            }
            
            // Filtrowanie po status
            if (status != null && !status.trim().isEmpty() && !status.equals("all")) {
                boolean activeFilter = status.equals("active");
                allUsers = allUsers.stream()
                        .filter(user -> {
                            boolean isActive = user.getActive() != null && user.getActive();
                            return activeFilter == isActive;
                        })
                        .collect(Collectors.toList());
            }
            
            // Paginacja
            int total = allUsers.size();
            int fromIndex = (page - 1) * limit;
            int toIndex = Math.min(fromIndex + limit, total);
            List<User> paginatedUsers = fromIndex < total 
                    ? allUsers.subList(fromIndex, toIndex)
                    : new ArrayList<>();
            
            List<Map<String, Object>> usersDto = paginatedUsers.stream()
                    .map(user -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id", IdMapper.toUserId(user.getId()));
                        dto.put("email", user.getEmail());
                        dto.put("username", user.getUsername());
                        dto.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
                        dto.put("active", user.getActive() != null ? user.getActive() : true);
                        dto.put("isAdmin", user.getIsAdmin() != null ? user.getIsAdmin() : false);
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                    "users", usersDto,
                    "total", total
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch users: " + e.getMessage()));
        }
    }

    /**
     * Nadaje/odbiera uprawnienia administratora użytkownikowi
     * PATCH /api/admin/users/{userId}/admin
     */
    @PatchMapping("/users/{userId}/admin")
    @Transactional
    public ResponseEntity<?> updateUserAdminStatus(
            @PathVariable String userId,
            @RequestBody Map<String, Object> request,
            @RequestParam(required = false) String adminCognitoSub) {
        try {
            Optional<User> userOpt = findUserById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID format or user not found"));
            }

            User user = userOpt.get();
            Boolean isAdmin = request.get("isAdmin") instanceof Boolean 
                    ? (Boolean) request.get("isAdmin")
                    : Boolean.parseBoolean(request.get("isAdmin").toString());
            
            user.setIsAdmin(isAdmin);
            user = userRepository.save(user);

            // Logowanie akcji
            logAdminAction(adminCognitoSub, "USER_ADMIN_STATUS_UPDATED", 
                    "Zmieniono uprawnienia administratora",
                    String.format("Uprawnienia administratora dla użytkownika %s zmienione na: %s", 
                            user.getEmail(), isAdmin ? "tak" : "nie"),
                    Map.of("userId", userId, "isAdmin", isAdmin));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", createUserDto(user));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user admin status: " + e.getMessage()));
        }
    }

    /**
     * Aktywuje/deaktywuje użytkownika
     * PATCH /api/admin/users/{userId}/status
     */
    @PatchMapping("/users/{userId}/status")
    @Transactional
    public ResponseEntity<?> updateUserStatus(
            @PathVariable String userId,
            @RequestBody Map<String, Object> request,
            @RequestParam(required = false) String adminCognitoSub) {
        try {
            Optional<User> userOpt = findUserById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID format or user not found"));
            }

            User user = userOpt.get();
            Boolean active = request.get("active") instanceof Boolean 
                    ? (Boolean) request.get("active")
                    : Boolean.parseBoolean(request.get("active").toString());
            
            user.setActive(active);
            user = userRepository.save(user);

            // Logowanie akcji
            logAdminAction(adminCognitoSub, "USER_STATUS_UPDATED", 
                    "Zmieniono status użytkownika",
                    String.format("Status użytkownika %s zmieniony na: %s", 
                            user.getEmail(), active ? "aktywny" : "nieaktywny"),
                    Map.of("userId", userId, "active", active));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", createUserDto(user));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user status: " + e.getMessage()));
        }
    }

    /**
     * Usuwa użytkownika
     * DELETE /api/admin/users/{userId}
     */
    @DeleteMapping("/users/{userId}")
    @Transactional
    public ResponseEntity<?> deleteUser(
            @PathVariable String userId,
            @RequestParam(required = false) String adminCognitoSub) {
        try {
            Optional<User> userOpt = findUserById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID format or user not found"));
            }

            User user = userOpt.get();
            String userEmail = user.getEmail();

            // Logowanie przed usunięciem
            logAdminAction(adminCognitoSub, "USER_DELETED",
                    "Usunięto użytkownika",
                    String.format("Użytkownik %s został usunięty", userEmail),
                    Map.of("userId", userId, "email", userEmail));

            userRepository.delete(user);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete user: " + e.getMessage()));
        }
    }

    /**
     * Pobiera statystyki systemowe
     * GET /api/admin/system/stats
     */
    @GetMapping("/system/stats")
    public ResponseEntity<?> getSystemStats() {
        try {
            long totalUsers = userRepository.count();
            long totalAquariums = aquariumRepository.count();
            
            // Zliczanie aktywnych użytkowników
            long activeUsers = userRepository.findAll().stream()
                    .filter(user -> user.getActive() != null && user.getActive())
                    .count();
            
            // Zliczanie wszystkich ryb we wszystkich akwariach
            long totalFish = aquariumFishRepository.findAll().stream()
                    .mapToLong(AquariumFish::getFishCount)
                    .sum();
            
            // Zliczanie wszystkich roślin we wszystkich akwariach
            long totalPlants = aquariumPlantRepository.findAll().stream()
                    .mapToLong(AquariumPlant::getPlantCount)
                    .sum();
            
            return ResponseEntity.ok(Map.of(
                    "totalUsers", totalUsers,
                    "totalAquariums", totalAquariums,
                    "totalFish", totalFish,
                    "totalPlants", totalPlants,
                    "activeUsers", activeUsers,
                    "inactiveUsers", totalUsers - activeUsers
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch system stats: " + e.getMessage()));
        }
    }

    /**
     * Pobiera listę akwariów
     * GET /api/admin/aquariums
     */
    @GetMapping("/aquariums")
    public ResponseEntity<?> getAquariums(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) String search) {
        try {
            List<Aquarium> allAquariums = aquariumRepository.findAll();
            
            // Filtrowanie po search (nazwa, właściciel)
            if (search != null && !search.trim().isEmpty()) {
                String searchLower = search.trim().toLowerCase();
                allAquariums = allAquariums.stream()
                        .filter(aquarium -> 
                            (aquarium.getName() != null && aquarium.getName().toLowerCase().contains(searchLower)) ||
                            (aquarium.getOwner() != null && aquarium.getOwner().getEmail() != null && 
                             aquarium.getOwner().getEmail().toLowerCase().contains(searchLower))
                        )
                        .collect(Collectors.toList());
            }
            
            // Paginacja
            int total = allAquariums.size();
            int fromIndex = (page - 1) * limit;
            int toIndex = Math.min(fromIndex + limit, total);
            List<Aquarium> paginatedAquariums = fromIndex < total 
                    ? allAquariums.subList(fromIndex, toIndex)
                    : new ArrayList<>();
            
            List<Map<String, Object>> aquariumsDto = paginatedAquariums.stream()
                    .map(aquarium -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id", IdMapper.toAquariumId(aquarium.getId()));
                        dto.put("name", aquarium.getName());
                        dto.put("owner", aquarium.getOwner() != null ? aquarium.getOwner().getEmail() : null);
                        dto.put("ownerId", aquarium.getOwner() != null ? IdMapper.toUserId(aquarium.getOwner().getId()) : null);
                        dto.put("waterType", aquarium.getWaterType());
                        dto.put("volumeLiters", aquarium.getVolumeLiters());
                        dto.put("createdAt", aquarium.getCreatedAt() != null ? aquarium.getCreatedAt().toString() : null);
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                    "aquariums", aquariumsDto,
                    "total", total
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch aquariums: " + e.getMessage()));
        }
    }

    /**
     * Usuwa akwarium
     * DELETE /api/admin/aquariums/{aquariumId}
     */
    @DeleteMapping("/aquariums/{aquariumId}")
    @Transactional
    public ResponseEntity<?> deleteAquarium(
            @PathVariable String aquariumId,
            @RequestParam(required = false) String adminCognitoSub) {
        try {
            Long aquariumIdLong = IdMapper.fromAquariumId(aquariumId);
            if (aquariumIdLong == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid aquarium ID format"));
            }

            Optional<Aquarium> aquariumOpt = aquariumRepository.findById(aquariumIdLong);
            if (aquariumOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Aquarium aquarium = aquariumOpt.get();
            String aquariumName = aquarium.getName();

            // Logowanie przed usunięciem
            logAdminAction(adminCognitoSub, "AQUARIUM_DELETED",
                    "Usunięto akwarium",
                    String.format("Akwarium '%s' zostało usunięte przez administratora", aquariumName),
                    Map.of("aquariumId", aquariumId, "aquariumName", aquariumName));

            // Usunięcie akwarium (kaskadowo usunie ryby i rośliny dzięki relacjom)
            aquariumRepository.delete(aquarium);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete aquarium: " + e.getMessage()));
        }
    }

    /**
     * Pobiera listę ryb we wszystkich akwariach
     * GET /api/admin/fish
     */
    @GetMapping("/fish")
    public ResponseEntity<?> getFish(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) Long aquariumId) {
        try {
            List<AquariumFish> allFish = aquariumFishRepository.findAll();
            
            // Filtrowanie po aquariumId jeśli podano
            if (aquariumId != null) {
                final Long aquariumIdFinal = aquariumId;
                allFish = allFish.stream()
                        .filter(fish -> fish.getAquarium() != null && 
                                       fish.getAquarium().getId().equals(aquariumIdFinal))
                        .collect(Collectors.toList());
            }
            
            // Paginacja
            int total = allFish.size();
            int fromIndex = (page - 1) * limit;
            int toIndex = Math.min(fromIndex + limit, total);
            List<AquariumFish> paginatedFish = fromIndex < total 
                    ? allFish.subList(fromIndex, toIndex)
                    : new ArrayList<>();
            
            List<Map<String, Object>> fishDto = paginatedFish.stream()
                    .map(fish -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id", fish.getId());
                        dto.put("speciesName", fish.getFishSpecies() != null ? fish.getFishSpecies().getName() : null);
                        dto.put("speciesId", fish.getFishSpecies() != null ? fish.getFishSpecies().getId() : null);
                        dto.put("aquariumName", fish.getAquarium() != null ? fish.getAquarium().getName() : null);
                        dto.put("aquariumId", fish.getAquarium() != null ? IdMapper.toAquariumId(fish.getAquarium().getId()) : null);
                        dto.put("owner", fish.getAquarium() != null && fish.getAquarium().getOwner() != null 
                                ? fish.getAquarium().getOwner().getEmail() : null);
                        dto.put("count", fish.getFishCount());
                        // createdAt nie jest dostępne w AquariumFish, więc używamy null
                        dto.put("createdAt", null);
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                    "fish", fishDto,
                    "total", total
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch fish: " + e.getMessage()));
        }
    }

    /**
     * Usuwa ryby z akwarium
     * DELETE /api/admin/fish/{fishId}
     */
    @DeleteMapping("/fish/{fishId}")
    @Transactional
    public ResponseEntity<?> deleteFish(
            @PathVariable Long fishId,
            @RequestParam(required = false) String adminCognitoSub) {
        try {
            Optional<AquariumFish> fishOpt = aquariumFishRepository.findById(fishId);
            if (fishOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            AquariumFish fish = fishOpt.get();
            String speciesName = fish.getFishSpecies() != null ? fish.getFishSpecies().getName() : "Unknown";
            String aquariumName = fish.getAquarium() != null ? fish.getAquarium().getName() : "Unknown";
            int count = fish.getFishCount();

            // Logowanie przed usunięciem
            logAdminAction(adminCognitoSub, "FISH_DELETED",
                    "Usunięto ryby z akwarium",
                    String.format("Usunięto %d %s z akwarium '%s'", count, speciesName, aquariumName),
                    Map.of("fishId", fishId, "speciesName", speciesName, 
                           "aquariumName", aquariumName, "count", count));

            aquariumFishRepository.delete(fish);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete fish: " + e.getMessage()));
        }
    }

    /**
     * Pobiera listę roślin we wszystkich akwariach
     * GET /api/admin/plants
     */
    @GetMapping("/plants")
    public ResponseEntity<?> getPlants(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) Long aquariumId) {
        try {
            List<AquariumPlant> allPlants = aquariumPlantRepository.findAll();
            
            // Filtrowanie po aquariumId jeśli podano
            if (aquariumId != null) {
                final Long aquariumIdFinal = aquariumId;
                allPlants = allPlants.stream()
                        .filter(plant -> plant.getAquarium() != null && 
                                       plant.getAquarium().getId().equals(aquariumIdFinal))
                        .collect(Collectors.toList());
            }
            
            // Paginacja
            int total = allPlants.size();
            int fromIndex = (page - 1) * limit;
            int toIndex = Math.min(fromIndex + limit, total);
            List<AquariumPlant> paginatedPlants = fromIndex < total 
                    ? allPlants.subList(fromIndex, toIndex)
                    : new ArrayList<>();
            
            List<Map<String, Object>> plantsDto = paginatedPlants.stream()
                    .map(plant -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id", plant.getId());
                        dto.put("plantName", plant.getPlant() != null ? plant.getPlant().getName() : null);
                        dto.put("plantId", plant.getPlant() != null ? plant.getPlant().getId() : null);
                        dto.put("aquariumName", plant.getAquarium() != null ? plant.getAquarium().getName() : null);
                        dto.put("aquariumId", plant.getAquarium() != null ? IdMapper.toAquariumId(plant.getAquarium().getId()) : null);
                        dto.put("owner", plant.getAquarium() != null && plant.getAquarium().getOwner() != null 
                                ? plant.getAquarium().getOwner().getEmail() : null);
                        dto.put("count", plant.getPlantCount());
                        // createdAt nie jest dostępne w AquariumPlant, więc używamy null
                        dto.put("createdAt", null);
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                    "plants", plantsDto,
                    "total", total
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch plants: " + e.getMessage()));
        }
    }

    /**
     * Usuwa rośliny z akwarium
     * DELETE /api/admin/plants/{plantId}
     */
    @DeleteMapping("/plants/{plantId}")
    @Transactional
    public ResponseEntity<?> deletePlant(
            @PathVariable Long plantId,
            @RequestParam(required = false) String adminCognitoSub) {
        try {
            Optional<AquariumPlant> plantOpt = aquariumPlantRepository.findById(plantId);
            if (plantOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            AquariumPlant plant = plantOpt.get();
            String plantName = plant.getPlant() != null ? plant.getPlant().getName() : "Unknown";
            String aquariumName = plant.getAquarium() != null ? plant.getAquarium().getName() : "Unknown";
            int count = plant.getPlantCount();

            // Logowanie przed usunięciem
            logAdminAction(adminCognitoSub, "PLANT_DELETED",
                    "Usunięto rośliny z akwarium",
                    String.format("Usunięto %d %s z akwarium '%s'", count, plantName, aquariumName),
                    Map.of("plantId", plantId, "plantName", plantName, 
                           "aquariumName", aquariumName, "count", count));

            aquariumPlantRepository.delete(plant);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete plant: " + e.getMessage()));
        }
    }

    /**
     * Pomocnicza metoda do walidacji i pobrania użytkownika po userId
     * @param userId ID użytkownika (może być w formacie z IdMapper)
     * @return Optional z użytkownikiem lub pusty Optional jeśli nie znaleziono
     */
    private Optional<User> findUserById(String userId) {
        String userIdString = IdMapper.fromUserId(userId);
        if (userIdString == null) {
            return Optional.empty();
        }
        return userRepository.findById(userIdString);
    }

    /**
     * Pomocnicza metoda do tworzenia DTO użytkownika
     */
    private Map<String, Object> createUserDto(User user) {
        Map<String, Object> userDto = new HashMap<>();
        userDto.put("id", IdMapper.toUserId(user.getId()));
        userDto.put("email", user.getEmail());
        userDto.put("username", user.getUsername());
        userDto.put("active", user.getActive());
        userDto.put("isAdmin", user.getIsAdmin());
        return userDto;
    }

    /**
     * Pomocnicza metoda do logowania akcji administratora
     * @param adminCognitoSub Cognito Sub administratora wykonującego akcję
     * @param actionType Typ akcji (np. "USER_ADMIN_STATUS_UPDATED")
     * @param title Tytuł wpisu logu
     * @param message Wiadomość wpisu logu
     * @param metadata Metadane akcji
     */
    private void logAdminAction(String adminCognitoSub, String actionType, String title, 
                                String message, Map<String, Object> metadata) {
        if (adminCognitoSub != null && !adminCognitoSub.trim().isEmpty()) {
            Optional<User> adminOpt = userRepository.findByCognitoSub(adminCognitoSub.trim());
            if (adminOpt.isPresent()) {
                createAdminLogEntry(adminOpt.get(), actionType, title, message, metadata);
            }
        }
    }

    /**
     * Pomocnicza metoda do tworzenia wpisów logów dla akcji administratora
     */
    private LogEntry createAdminLogEntry(User admin, String actionType, String title, 
                                         String message, Map<String, Object> metadata) {
        try {
            LogEntry logEntry = new LogEntry();
            logEntry.setUser(admin);
            logEntry.setAquarium(null);
            logEntry.setAquariumName(null);
            logEntry.setActionType(actionType);
            logEntry.setTitle(title);
            logEntry.setMessage(message);
            
            if (metadata != null && !metadata.isEmpty()) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    logEntry.setMetadata(objectMapper.writeValueAsString(metadata));
                } catch (Exception e) {
                    logEntry.setMetadata(metadata.toString());
                }
            }
            
            logEntry.setCreatedAt(LocalDateTime.now());
            return logEntryRepository.save(logEntry);
        } catch (Exception e) {
            // Jeśli logowanie się nie powiedzie, nie przerywamy głównej operacji
            return null;
        }
    }
}
