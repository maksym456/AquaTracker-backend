package com.aquatracker.user;

import com.aquatracker.common.IdMapper;

public class UserResponseDto {
    private String id; // UUID
    private String username;
    private String email;
    private String language;
    private String theme;

    public UserResponseDto() {}

    public UserResponseDto(User user) {
        // User.id jest teraz UUID (String), używamy IdMapper dla spójności z API
        this.id = IdMapper.toUserId(user.getId());
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.language = user.getSettingsLanguage() != null ? user.getSettingsLanguage() : "pl";
        this.theme = user.getSettingsTheme() != null ? user.getSettingsTheme() : "light";
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}

