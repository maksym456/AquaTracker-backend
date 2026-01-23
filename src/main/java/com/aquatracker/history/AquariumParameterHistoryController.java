package com.aquatracker.history;

import com.aquatracker.common.IdMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Profile("!dev")
@RestController
@RequestMapping("/api/v1/history")
public class AquariumParameterHistoryController {

    private final AquariumParameterHistoryRepository historyRepository;

    public AquariumParameterHistoryController(
            AquariumParameterHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @GetMapping("/aquarium/{aquariumId}")
    public ResponseEntity<?> getHistoryByAquarium(@PathVariable String aquariumId) {
        try {
            Long aqId = IdMapper.fromAquariumId(aquariumId);
            if (aqId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid aquarium ID"));
            }

            List<AquariumParameterHistory> history = historyRepository.findByAquarium_IdOrderByChangedAtDesc(aqId);
            List<AquariumParameterHistoryResponseDto> response = history.stream()
                    .map(AquariumParameterHistoryResponseDto::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch history: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getHistoryByUser(@PathVariable String userId) {
        try {
            String uId = IdMapper.fromUserId(userId);
            if (uId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID (expected UUID)"));
            }

            List<AquariumParameterHistory> history = historyRepository.findByUser_IdOrderByChangedAtDesc(uId);
            List<AquariumParameterHistoryResponseDto> response = history.stream()
                    .map(AquariumParameterHistoryResponseDto::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch history: " + e.getMessage()));
        }
    }

    @GetMapping("/aquarium/{aquariumId}/parameter/{parameterName}")
    public ResponseEntity<?> getHistoryByParameter(
            @PathVariable String aquariumId,
            @PathVariable String parameterName) {
        try {
            Long aqId = IdMapper.fromAquariumId(aquariumId);
            if (aqId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid aquarium ID"));
            }

            List<AquariumParameterHistory> history = historyRepository
                    .findByAquarium_IdAndParameterNameOrderByChangedAtDesc(aqId, parameterName);
            List<AquariumParameterHistoryResponseDto> response = history.stream()
                    .map(AquariumParameterHistoryResponseDto::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch history: " + e.getMessage()));
        }
    }
}

