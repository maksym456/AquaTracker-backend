package com.aquatracker;

import com.aquatracker.common.IdMapper;
import com.aquatracker.user.User;
import com.aquatracker.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * AuthController - obsługuje autoryzację z AWS Cognito
 * 
 * UWAGA: Logowanie i rejestracja odbywają się bezpośrednio między Frontend a AWS Cognito.
 * Backend nie uczestniczy w procesie autoryzacji - tylko weryfikuje tokeny JWT i zwraca dane użytkownika.
 * 
 * Synchronizacja użytkowników z Cognito odbywa się przez POST /api/v1/users/sync
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Pobiera dane zalogowanego użytkownika
     * GET /api/v1/auth/me
     * 
     * Wymaga nagłówka Authorization z tokenem JWT z AWS Cognito.
     * CognitoSub (sub) będzie wyciągane z tokenu przez SecurityConfig (do zaimplementowania przez Maksyma).
     * 
     * Na razie przyjmuje cognitoSub jako query parameter dla testów (TEMP - do usunięcia po implementacji JWT).
     * 
     * @param cognitoSub - TYLKO DO TESTOWANIA - po implementacji JWT będzie wyciągane z tokenu
     * @return Dane użytkownika z lokalnej bazy danych
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestParam(required = false) String cognitoSub) {
        try {
            // TODO: Po implementacji JWT przez Maksyma, wyciągnij cognitoSub z tokenu:
            // String cognitoSub = extractCognitoSubFromJWT(request);
            // Na razie używamy query parameter tylko do testów
            
            if (cognitoSub == null || cognitoSub.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                            "error", "JWT token authentication not implemented yet",
                            "message", "Please provide cognitoSub as query parameter for testing. " +
                                      "After JWT implementation, cognitoSub will be extracted from Authorization header."
                        ));
            }

            Optional<User> userOpt = userRepository.findByCognitoSub(cognitoSub.trim());
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "error", "User not found",
                            "message", "User with this cognitoSub does not exist in local database. " +
                                      "Please sync user first using POST /api/v1/users/sync"
                        ));
            }

            User user = userOpt.get();
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", IdMapper.toUserId(user.getId()));
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("cognitoSub", user.getCognitoSub());
            userData.put("createdAt", user.getCreatedAt());
            userData.put("settingsLanguage", user.getSettingsLanguage());
            userData.put("settingsTheme", user.getSettingsTheme());
            userData.put("settingsSessionLengthMinutes", user.getSettingsSessionLengthMinutes());
            userData.put("settingsDataSource", user.getSettingsDataSource());

            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch current user: " + e.getMessage()));
        }
    }
}

