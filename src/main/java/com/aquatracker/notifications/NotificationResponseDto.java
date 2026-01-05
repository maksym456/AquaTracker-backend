package com.aquatracker.notifications;

import com.aquatracker.common.IdMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

public class NotificationResponseDto {
    private String id;
    private String userId;
    private String aquariumId;
    private String aquariumName;
    private String notificationType;
    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private Map<String, Object> metadata;

    public NotificationResponseDto() {}

    public NotificationResponseDto(Notification notification) {
        this.id = IdMapper.toNotificationId(notification.getId());
        this.userId = IdMapper.toUserId(notification.getUser().getId());
        this.aquariumId = notification.getAquarium() != null ? IdMapper.toAquariumId(notification.getAquarium().getId()) : null;
        this.aquariumName = notification.getAquarium() != null ? notification.getAquarium().getName() : null;
        this.notificationType = notification.getNotificationType();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.isRead = notification.getIsRead();
        this.readAt = notification.getReadAt();
        this.createdAt = notification.getCreatedAt();
        
        // Parse metadata JSON if exists
        if (notification.getMetadata() != null && !notification.getMetadata().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.metadata = mapper.readValue(notification.getMetadata(), Map.class);
            } catch (Exception e) {
                this.metadata = Map.of("raw", notification.getMetadata());
            }
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAquariumId() {
        return aquariumId;
    }

    public void setAquariumId(String aquariumId) {
        this.aquariumId = aquariumId;
    }

    public String getAquariumName() {
        return aquariumName;
    }

    public void setAquariumName(String aquariumName) {
        this.aquariumName = aquariumName;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}

