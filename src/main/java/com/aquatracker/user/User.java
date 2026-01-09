package com.aquatracker.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "username")
    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Email musi być poprawny")
    private String email;

    /**
     * @deprecated Pole password nie jest używane przy autoryzacji przez AWS Cognito.
     * Hasła są zarządzane przez AWS Cognito, nie przechowujemy ich lokalnie.
     * Pole pozostaje dla kompatybilności wstecznej, ale powinno być zawsze puste.
     */
    /**
     * @deprecated Pole password nie jest używane przy autoryzacji przez AWS Cognito.
     * Hasła są zarządzane przez AWS Cognito, nie przechowujemy ich lokalnie.
     * Pole pozostaje dla kompatybilności wstecznej, ale powinno być zawsze puste.
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Column(name = "password", nullable = true)
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

    @Column(name = "cognito_sub", unique = true, nullable = true)
    private String cognitoSub;

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

    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public String getPassword() {
        return password;
    }

    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
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

    public String getCognitoSub() {
        return cognitoSub;
    }

    public void setCognitoSub(String cognitoSub) {
        this.cognitoSub = cognitoSub;
    }
}