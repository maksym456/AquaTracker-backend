package com.aquatracker.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldGetUserByIdWhenFound() {
        User user = new User();
        String userId = "00000000-0000-4000-8000-000000000001";
        user.setId(userId);
        user.setUsername("testuser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.getUserById(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(UserResponseDto.class);
    }

    @Test
    void shouldReturnNotFoundWhenUserNotFound() {
        String userId = "00000000-0000-4000-8000-000000000001";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.getUserById(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}