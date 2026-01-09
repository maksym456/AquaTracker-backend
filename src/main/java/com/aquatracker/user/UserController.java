package com.aquatracker.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Synchronizuje użytkownika z AWS Cognito
     * POST /api/v1/users/sync
     * Body: { "cognitoSub": "uuid", "email": "email@example.com", "username": "username" }
     * 
     * Jeśli użytkownik istnieje (po cognitoSub lub email), aktualizuje go.
     * Jeśli nie istnieje, tworzy nowego użytkownika.
     */
    @PostMapping("/sync")
    @Transactional
    public ResponseEntity<?> syncUserFromCognito(@RequestBody SyncUserRequest request) {
        try {
            if (request.getCognitoSub() == null || request.getCognitoSub().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "cognitoSub is required"));
            }

            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "email is required"));
            }

            String cognitoSub = request.getCognitoSub().trim();
            String email = request.getEmail().trim().toLowerCase();
            String username = request.getUsername() != null ? request.getUsername().trim() : 
                            email.split("@")[0]; // Domyślnie username z email

            // Sprawdź czy użytkownik istnieje po cognitoSub
            Optional<User> existingUserBySub = userRepository.findByCognitoSub(cognitoSub);
            
            // Sprawdź czy użytkownik istnieje po email
            Optional<User> existingUserByEmail = userRepository.findByEmail(email);

            User user;

            if (existingUserBySub.isPresent()) {
                // Użytkownik istnieje po cognitoSub - aktualizuj
                user = existingUserBySub.get();
                user.setEmail(email);
                if (username != null && !username.isEmpty()) {
                    user.setUsername(username);
                }
                // Aktualizuj cognitoSub jeśli się zmienił
                if (!cognitoSub.equals(user.getCognitoSub())) {
                    user.setCognitoSub(cognitoSub);
                }
            } else if (existingUserByEmail.isPresent()) {
                // Użytkownik istnieje po email, ale nie ma cognitoSub - dodaj cognitoSub
                user = existingUserByEmail.get();
                user.setCognitoSub(cognitoSub);
                if (username != null && !username.isEmpty()) {
                    user.setUsername(username);
                }
            } else {
                // Nowy użytkownik - utwórz
                user = new User();
                user.setCognitoSub(cognitoSub);
                user.setEmail(email);
                user.setUsername(username);
                user.setPassword(""); // Hasło nie jest przechowywane lokalnie (Cognito zarządza)
                user.setCreatedAt(LocalDateTime.now());
            }

            user = userRepository.save(user);

            return ResponseEntity.ok(new UserResponseDto(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to sync user: " + e.getMessage()));
        }
    }

    /**
     * Ręczne dodawanie użytkownika do bazy danych
     * POST /api/v1/users
     * Body: { "username": "username", "email": "email@example.com", "cognitoSub": "uuid" (opcjonalne) }
     */
    @PostMapping
    @Transactional
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "email is required"));
            }

            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "username is required"));
            }

            String email = request.getEmail().trim().toLowerCase();
            String username = request.getUsername().trim();

            // Sprawdź czy użytkownik już istnieje
            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "User with this email already exists"));
            }

            // Jeśli podano cognitoSub, sprawdź czy nie jest już używany
            if (request.getCognitoSub() != null && !request.getCognitoSub().trim().isEmpty()) {
                String cognitoSub = request.getCognitoSub().trim();
                if (userRepository.existsByCognitoSub(cognitoSub)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("error", "User with this cognitoSub already exists"));
                }
            }

            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            if (request.getCognitoSub() != null && !request.getCognitoSub().trim().isEmpty()) {
                user.setCognitoSub(request.getCognitoSub().trim());
            }
            user.setPassword(""); // Hasło nie jest przechowywane lokalnie
            user.setCreatedAt(LocalDateTime.now());

            user = userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new UserResponseDto(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create user: " + e.getMessage()));
        }
    }

    /**
     * Pobiera użytkownika po ID
     * GET /api/v1/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        try {
            String userIdString = com.aquatracker.common.IdMapper.fromUserId(userId);
            if (userIdString == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID format (expected UUID)"));
            }

            return userRepository.findById(userIdString)
                    .map(user -> ResponseEntity.ok(new UserResponseDto(user)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user: " + e.getMessage()));
        }
    }

    /**
     * Pobiera użytkownika po cognitoSub
     * GET /api/v1/users/cognito/{cognitoSub}
     */
    @GetMapping("/cognito/{cognitoSub}")
    public ResponseEntity<?> getUserByCognitoSub(@PathVariable String cognitoSub) {
        try {
            return userRepository.findByCognitoSub(cognitoSub)
                    .map(user -> ResponseEntity.ok(new UserResponseDto(user)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user: " + e.getMessage()));
        }
    }

    /**
     * Pobiera wszystkich użytkowników (dla administracji)
     * GET /api/v1/users
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserResponseDto> users = userRepository.findAll().stream()
                    .map(UserResponseDto::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch users: " + e.getMessage()));
        }
    }

    /**
     * Aktualizuje użytkownika
     * PUT /api/v1/users/{userId}
     */
    @PutMapping("/{userId}")
    @Transactional
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UpdateUserRequest request) {
        try {
            String userIdString = com.aquatracker.common.IdMapper.fromUserId(userId);
            if (userIdString == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID format (expected UUID)"));
            }

            return userRepository.findById(userIdString)
                    .map(user -> {
                        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
                            user.setUsername(request.getUsername().trim());
                        }
                        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                            String newEmail = request.getEmail().trim().toLowerCase();
                            // Sprawdź czy email nie jest już używany przez innego użytkownika
                            if (!newEmail.equals(user.getEmail())) {
                                if (userRepository.existsByEmail(newEmail)) {
                                    throw new RuntimeException("Email already exists");
                                }
                                user.setEmail(newEmail);
                            }
                        }
                        if (request.getSettingsLanguage() != null) {
                            user.setSettingsLanguage(request.getSettingsLanguage());
                        }
                        if (request.getSettingsTheme() != null) {
                            user.setSettingsTheme(request.getSettingsTheme());
                        }
                        if (request.getSettingsSessionLengthMinutes() != null) {
                            user.setSettingsSessionLengthMinutes(request.getSettingsSessionLengthMinutes());
                        }
                        if (request.getSettingsDataSource() != null) {
                            user.setSettingsDataSource(request.getSettingsDataSource());
                        }

                        user = userRepository.save(user);
                        return ResponseEntity.ok(new UserResponseDto(user));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user: " + e.getMessage()));
        }
    }

    public static class SyncUserRequest {
        private String cognitoSub;
        private String email;
        private String username;

        public String getCognitoSub() {
            return cognitoSub;
        }

        public void setCognitoSub(String cognitoSub) {
            this.cognitoSub = cognitoSub;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public static class CreateUserRequest {
        private String username;
        private String email;
        private String cognitoSub;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCognitoSub() {
            return cognitoSub;
        }

        public void setCognitoSub(String cognitoSub) {
            this.cognitoSub = cognitoSub;
        }
    }

    public static class UpdateUserRequest {
        private String username;
        private String email;
        private String settingsLanguage;
        private String settingsTheme;
        private Integer settingsSessionLengthMinutes;
        private String settingsDataSource;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSettingsLanguage() {
            return settingsLanguage;
        }

        public void setSettingsLanguage(String settingsLanguage) {
            this.settingsLanguage = settingsLanguage;
        }

        public String getSettingsTheme() {
            return settingsTheme;
        }

        public void setSettingsTheme(String settingsTheme) {
            this.settingsTheme = settingsTheme;
        }

        public Integer getSettingsSessionLengthMinutes() {
            return settingsSessionLengthMinutes;
        }

        public void setSettingsSessionLengthMinutes(Integer settingsSessionLengthMinutes) {
            this.settingsSessionLengthMinutes = settingsSessionLengthMinutes;
        }

        public String getSettingsDataSource() {
            return settingsDataSource;
        }

        public void setSettingsDataSource(String settingsDataSource) {
            this.settingsDataSource = settingsDataSource;
        }
    }
}

