package com.aquatracker.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    private java.time.LocalDateTime createdAt;

    private String settingsLanguage;
    private String settingsTheme;
    private Integer settingsSessionLengthMinutes;
    private String settingsDataSource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Nowe gettery i settery
    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSettingsLanguage() {
        return settingsLanguage != null ? settingsLanguage : "pl";
    }

    public void setSettingsLanguage(String settingsLanguage) {
        this.settingsLanguage = settingsLanguage;
    }

    public String getSettingsTheme() {
        return settingsTheme != null ? settingsTheme : "light";
    }

    public void setSettingsTheme(String settingsTheme) {
        this.settingsTheme = settingsTheme;
    }

    public Integer getSettingsSessionLengthMinutes() {
        return settingsSessionLengthMinutes != null ? settingsSessionLengthMinutes : 60;
    }

    public void setSettingsSessionLengthMinutes(Integer settingsSessionLengthMinutes) {
        this.settingsSessionLengthMinutes = settingsSessionLengthMinutes;
    }

    public String getSettingsDataSource() {
        return settingsDataSource != null ? settingsDataSource : "production";
    }

    public void setSettingsDataSource(String settingsDataSource) {
        this.settingsDataSource = settingsDataSource;
    }
}