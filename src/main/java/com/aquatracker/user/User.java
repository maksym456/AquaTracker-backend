package com.aquatracker.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Email musi być poprawny")
    private String email;

    @Column(name = "password")
    @NotBlank(message = "Hasło jest wymagane")
    @Size(min = 8, message = "Hasło musi mieć co najmniej 8 znaków")
    private String password;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "settings_language")
    private String settingsLanguage;
    
    @Column(name = "settings_theme")
    private String settingsTheme;
    
    @Column(name = "settings_session_length_minutes")
    private Integer settingsSessionLengthMinutes;
    
    @Column(name = "settings_data_source")
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