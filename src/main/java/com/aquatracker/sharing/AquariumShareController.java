package com.aquatracker.sharing;

import com.aquatracker.aquarium.Aquarium;
import com.aquatracker.aquarium.AquariumRepository;
import com.aquatracker.common.IdMapper;
import com.aquatracker.user.User;
import com.aquatracker.user.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Profile("!dev")
@RestController
@RequestMapping("/api/v1/aquariums/{aquariumId}/shares")
public class AquariumShareController {

    private final AquariumShareRepository shareRepository;
    private final AquariumRepository aquariumRepository;
    private final UserRepository userRepository;

    public AquariumShareController(
            AquariumShareRepository shareRepository,
            AquariumRepository aquariumRepository,
            UserRepository userRepository) {
        this.shareRepository = shareRepository;
        this.aquariumRepository = aquariumRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getShares(@PathVariable String aquariumId) {
        try {
            Long aqId = IdMapper.fromAquariumId(aquariumId);
            if (aqId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid aquarium ID"));
            }

            List<AquariumShare> shares = shareRepository.findByAquarium_Id(aqId);
            List<AquariumShareResponseDto> response = shares.stream()
                    .map(AquariumShareResponseDto::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch shares: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> shareAquarium(
            @PathVariable String aquariumId,
            @RequestBody Map<String, String> request) {
        try {
            Long aqId = IdMapper.fromAquariumId(aquariumId);
            if (aqId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid aquarium ID"));
            }

            String userId = request.get("userId");
            String permissionLevel = request.getOrDefault("permissionLevel", "read");
            String sharedById = request.get("sharedBy");

            if (userId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "userId is required"));
            }

            String uId = IdMapper.fromUserId(userId);
            if (uId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID (expected UUID)"));
            }

            Optional<Aquarium> aquariumOpt = aquariumRepository.findById(aqId);
            if (aquariumOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<User> userOpt = userRepository.findById(uId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User not found"));
            }

            // Check if already shared
            Optional<AquariumShare> existingShare = shareRepository.findByAquarium_IdAndUser_Id(aqId, uId);
            if (existingShare.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Aquarium already shared with this user"));
            }

            Aquarium aquarium = aquariumOpt.get();
            User user = userOpt.get();
            String sharedBy = sharedById != null ? IdMapper.fromUserId(sharedById) : null;

            AquariumShare share = new AquariumShare(aquarium, user, permissionLevel, sharedBy);
            share = shareRepository.save(share);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AquariumShareResponseDto(share));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to share aquarium: " + e.getMessage()));
        }
    }

    @PutMapping("/{shareId}")
    public ResponseEntity<?> updateSharePermission(
            @PathVariable String aquariumId,
            @PathVariable String shareId,
            @RequestBody Map<String, String> request) {
        try {
            Long sId = IdMapper.fromShareId(shareId);
            if (sId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid share ID"));
            }

            Optional<AquariumShare> shareOpt = shareRepository.findById(sId);
            if (shareOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            AquariumShare share = shareOpt.get();
            String permissionLevel = request.get("permissionLevel");
            if (permissionLevel != null) {
                share.setPermissionLevel(permissionLevel);
                share = shareRepository.save(share);
            }

            return ResponseEntity.ok(new AquariumShareResponseDto(share));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update share: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{shareId}")
    public ResponseEntity<?> unshareAquarium(
            @PathVariable String aquariumId,
            @PathVariable String shareId) {
        try {
            Long sId = IdMapper.fromShareId(shareId);
            if (sId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid share ID"));
            }

            shareRepository.deleteById(sId);
            return ResponseEntity.ok(Map.of("message", "Aquarium unshared successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to unshare aquarium: " + e.getMessage()));
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> unshareFromUser(
            @PathVariable String aquariumId,
            @PathVariable String userId) {
        try {
            Long aqId = IdMapper.fromAquariumId(aquariumId);
            String uId = IdMapper.fromUserId(userId);

            if (aqId == null || uId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid IDs"));
            }

            shareRepository.deleteByAquarium_IdAndUser_Id(aqId, uId);
            return ResponseEntity.ok(Map.of("message", "Aquarium unshared successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to unshare aquarium: " + e.getMessage()));
        }
    }
}

