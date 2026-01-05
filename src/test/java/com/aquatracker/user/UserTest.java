package com.aquatracker.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldSetAndGetAllBasicFieldsCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setUsername("janek123");
        user.setEmail("janek@example.com");
        user.setCreatedAt(LocalDateTime.of(2025, 12, 29, 12, 0));

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("janek123");
        assertThat(user.getEmail()).isEqualTo("janek@example.com");
        assertThat(user.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 12, 29, 12, 0));
    }

    @Test
    void shouldPassValidationWhenAllFieldsValid() {
        User user = new User();
        user.setUsername("validuser");
        user.setEmail("valid@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationWhenUsernameBlank() {
        User user = new User();
        user.setEmail("valid@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("wymagana");
    }

    @Test
    void shouldFailValidationWhenEmailInvalid() {
        User user = new User();
        user.setUsername("validuser");
        user.setEmail("invalid-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("poprawny");
    }

    @Test
    void shouldFailWithMultipleViolations() {
        User user = new User();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).hasSize(2);  
    }
}