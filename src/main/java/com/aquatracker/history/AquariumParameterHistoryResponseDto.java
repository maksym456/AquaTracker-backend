package com.aquatracker.history;

import com.aquatracker.common.IdMapper;

import java.time.LocalDateTime;

public class AquariumParameterHistoryResponseDto {
    private String id;
    private String aquariumId;
    private String userId;
    private String parameterName;
    private String oldValue;
    private String newValue;
    private LocalDateTime changedAt;
    private String description;

    public AquariumParameterHistoryResponseDto() {}

    public AquariumParameterHistoryResponseDto(AquariumParameterHistory history) {
        this.id = IdMapper.toHistoryId(history.getId());
        this.aquariumId = IdMapper.toAquariumId(history.getAquarium().getId());
        this.userId = IdMapper.toUserId(history.getUser().getId());
        this.parameterName = history.getParameterName();
        this.oldValue = history.getOldValue();
        this.newValue = history.getNewValue();
        this.changedAt = history.getChangedAt();
        this.description = history.getDescription();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAquariumId() {
        return aquariumId;
    }

    public void setAquariumId(String aquariumId) {
        this.aquariumId = aquariumId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

