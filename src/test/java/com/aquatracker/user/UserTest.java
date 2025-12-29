package com.aquatracker.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void shouldSetAndGetAllBasicFieldsCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setUsername("janek123");
        user.setEmail("janek@example.com");
        user.setPassword("superTajneHaslo123");
        user.setCreatedAt(LocalDateTime.of(2025, 12, 29, 12, 0));
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("janek123");
        assertThat(user.getEmail()).isEqualTo("janek@example.com");
        assertThat(user.getPassword()).isEqualTo("superTajneHaslo123");
        assertThat(user.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 12, 29, 12, 0));
    }

    @Test
    void shouldUseDefaultValuesForSettingsWhenNull() {
        User user = new User();
        assertThat(user.getSettingsLanguage()).isEqualTo("pl");
        assertThat(user.getSettingsTheme()).isEqualTo("light");
        assertThat(user.getSettingsSessionLengthMinutes()).isEqualTo(60);
        assertThat(user.getSettingsDataSource()).isEqualTo("production");
    }

    @Test
    void shouldAllowOverridingDefaultSettings() {
        User user = new User();
        user.setSettingsLanguage("en");
        user.setSettingsTheme("dark");
        user.setSettingsSessionLengthMinutes(120);
        user.setSettingsDataSource("test");
        assertThat(user.getSettingsLanguage()).isEqualTo("en");
        assertThat(user.getSettingsTheme()).isEqualTo("dark");
        assertThat(user.getSettingsSessionLengthMinutes()).isEqualTo(120);
        assertThat(user.getSettingsDataSource()).isEqualTo("test");
    }

    @Test
    void shouldMixCustomAndDefaultSettings() {
        User user = new User();
        user.setSettingsLanguage("de");
        user.setSettingsSessionLengthMinutes(30);
        assertThat(user.getSettingsLanguage()).isEqualTo("de");
        assertThat(user.getSettingsTheme()).isEqualTo("light"); 
        assertThat(user.getSettingsSessionLengthMinutes()).isEqualTo(30);
        assertThat(user.getSettingsDataSource()).isEqualTo("production");  
    }

    @Test
    void shouldHaveCorrectDefaultValuesForNewUser() {
        User user = new User();
        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.getCreatedAt()).isNull();
        assertThat(user.getSettingsLanguage()).isEqualTo("pl");
        assertThat(user.getSettingsTheme()).isEqualTo("light");
        assertThat(user.getSettingsSessionLengthMinutes()).isEqualTo(60);
        assertThat(user.getSettingsDataSource()).isEqualTo("production");
    }

    @Test
    void shouldAllowSettingNullForOverridableSettings() {
        User user = new User();
        user.setSettingsLanguage(null);
        user.setSettingsTheme(null);
        user.setSettingsSessionLengthMinutes(null);
        user.setSettingsDataSource(null);
        assertThat(user.getSettingsLanguage()).isEqualTo("pl");
        assertThat(user.getSettingsTheme()).isEqualTo("light");
        assertThat(user.getSettingsSessionLengthMinutes()).isEqualTo(60);
        assertThat(user.getSettingsDataSource()).isEqualTo("production");
    }
}