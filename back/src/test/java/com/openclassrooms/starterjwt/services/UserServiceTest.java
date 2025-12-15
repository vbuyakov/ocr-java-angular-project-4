package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();
    }

    @Test
    void testFindById_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        verify(userRepository).findById(userId);
    }

    @Test
    void testFindById_NotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository).findById(userId);
    }

    @Test
    void testDelete_Success() {
        // Given
        Long userId = 1L;
        String currentUserEmail = "test@example.com";
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(userId);

        // When
        userService.delete(userId, currentUserEmail);

        // Then
        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDelete_Unauthorized() {
        // Given
        Long userId = 1L;
        String currentUserEmail = "other@example.com";
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When/Then
        assertThatThrownBy(() -> userService.delete(userId, currentUserEmail))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unauthorized");
        verify(userRepository).findById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDelete_UserNotFound() {
        // Given
        Long userId = 999L;
        String currentUserEmail = "test@example.com";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.delete(userId, currentUserEmail))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository).findById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDelete_SameEmailButDifferentCase() {
        // Given
        Long userId = 1L;
        String currentUserEmail = "TEST@EXAMPLE.COM"; // Different case
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When/Then
        assertThatThrownBy(() -> userService.delete(userId, currentUserEmail))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unauthorized");
        verify(userRepository).findById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }
}

