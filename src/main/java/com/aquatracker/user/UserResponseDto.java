package com.aquatracker.user;

import com.aquatracker.common.IdMapper;

import java.time.LocalDateTime;

public class UserResponseDto {
    private String id;
    private String username;
    private String email;
    private String cognitoSub;
    private LocalDateTime createdAt;
    private String settingsLanguage;
    private String settingsTheme;
    private Integer settingsSessionLengthMinutes;
    private String settingsDataSource;

    public UserResponseDto() {}

    public UserResponseDto(User user) {
        this.id = IdMapper.toUserId(user.getId());
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.cognitoSub = user.getCognitoSub();
        this.createdAt = user.getCreatedAt();
        this.settingsLanguage = user.getSettingsLanguage();
        this.settingsTheme = user.getSettingsTheme();
        this.settingsSessionLengthMinutes = user.getSettingsSessionLengthMinutes();
        this.settingsDataSource = user.getSettingsDataSource();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

