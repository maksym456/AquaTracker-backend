package com.aquatracker.sharing;

import com.aquatracker.common.IdMapper;

import java.time.LocalDateTime;

public class AquariumShareResponseDto {
    private String id;
    private String aquariumId;
    private String userId;
    private String userName;
    private String userEmail;
    private String permissionLevel;
    private String sharedById;
    private LocalDateTime sharedAt;

    public AquariumShareResponseDto() {}

    public AquariumShareResponseDto(AquariumShare share) {
        this.id = IdMapper.toShareId(share.getId());
        this.aquariumId = IdMapper.toAquariumId(share.getAquarium().getId());
        this.userId = IdMapper.toUserId(share.getUser().getId());
        this.userName = share.getUser().getUsername();
        this.userEmail = share.getUser().getEmail();
        this.permissionLevel = share.getPermissionLevel();
        this.sharedById = share.getSharedBy() != null ? IdMapper.toUserId(share.getSharedBy()) : null;
        this.sharedAt = share.getSharedAt();
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public String getSharedById() {
        return sharedById;
    }

    public void setSharedById(String sharedById) {
        this.sharedById = sharedById;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }
}

