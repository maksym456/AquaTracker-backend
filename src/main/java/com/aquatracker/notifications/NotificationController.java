package com.aquatracker.notifications;

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
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AquariumRepository aquariumRepository;

    public NotificationController(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            AquariumRepository aquariumRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.aquariumRepository = aquariumRepository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getNotifications(
            @PathVariable String userId,
            @RequestParam(required = false) Boolean read) {
        try {
            String uId = IdMapper.fromUserId(userId);
            if (uId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID (expected UUID)"));
            }

            List<Notification> notifications;
            if (read != null) {
                notifications = notificationRepository.findByUser_IdAndIsReadOrderByCreatedAtDesc(uId, read);
            } else {
                notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(uId);
            }

            List<NotificationResponseDto> response = notifications.stream()
                    .map(NotificationResponseDto::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch notifications: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<?> getUnreadCount(@PathVariable String userId) {
        try {
            String uId = IdMapper.fromUserId(userId);
            if (uId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID (expected UUID)"));
            }

            Long count = notificationRepository.countByUser_IdAndIsRead(uId, false);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch count: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String notificationType = (String) request.get("notificationType");
            String title = (String) request.get("title");
            String message = (String) request.get("message");
            String aquariumId = (String) request.get("aquariumId");
            String metadata = (String) request.get("metadata");

            if (userId == null || notificationType == null || title == null || message == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing required fields"));
            }

            String uId = IdMapper.fromUserId(userId);
            if (uId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID (expected UUID)"));
            }

            Optional<User> userOpt = userRepository.findById(uId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User not found"));
            }

            Notification notification = new Notification(userOpt.get(), notificationType, title, message);
            notification.setMetadata(metadata);

            if (aquariumId != null) {
                Long aqId = IdMapper.fromAquariumId(aquariumId);
                if (aqId != null) {
                    Optional<Aquarium> aquariumOpt = aquariumRepository.findById(aqId);
                    aquariumOpt.ifPresent(notification::setAquarium);
                }
            }

            notification = notificationRepository.save(notification);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new NotificationResponseDto(notification));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create notification: " + e.getMessage()));
        }
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String notificationId) {
        try {
            Long nId = IdMapper.fromNotificationId(notificationId);
            if (nId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid notification ID"));
            }

            Optional<Notification> notificationOpt = notificationRepository.findById(nId);
            if (notificationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Notification notification = notificationOpt.get();
            notification.setIsRead(true);
            notification = notificationRepository.save(notification);

            return ResponseEntity.ok(new NotificationResponseDto(notification));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to mark as read: " + e.getMessage()));
        }
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable String userId) {
        try {
            String uId = IdMapper.fromUserId(userId);
            if (uId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid user ID (expected UUID)"));
            }

            List<Notification> unread = notificationRepository.findByUser_IdAndIsReadOrderByCreatedAtDesc(uId, false);
            unread.forEach(n -> n.setIsRead(true));
            notificationRepository.saveAll(unread);

            return ResponseEntity.ok(Map.of("message", "All notifications marked as read", "count", unread.size()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to mark all as read: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable String notificationId) {
        try {
            Long nId = IdMapper.fromNotificationId(notificationId);
            if (nId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid notification ID"));
            }

            notificationRepository.deleteById(nId);
            return ResponseEntity.ok(Map.of("message", "Notification deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete notification: " + e.getMessage()));
        }
    }
}

