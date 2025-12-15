package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

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
    void testLoadUserByUsername_Success() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(UserDetailsImpl.class);
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        assertThat(userDetailsImpl.getId()).isEqualTo(1L);
        assertThat(userDetailsImpl.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetailsImpl.getFirstName()).isEqualTo("John");
        assertThat(userDetailsImpl.getLastName()).isEqualTo("Doe");
        assertThat(userDetailsImpl.getPassword()).isEqualTo("encodedPassword");
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Given
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User Not Found with email: " + email);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testLoadUserByUsername_AdminUser() {
        // Given
        User adminUser = User.builder()
                .id(2L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("encodedPassword")
                .admin(true)
                .build();

        String email = "admin@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(adminUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(UserDetailsImpl.class);
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        assertThat(userDetailsImpl.getId()).isEqualTo(2L);
        assertThat(userDetailsImpl.getUsername()).isEqualTo("admin@example.com");
        assertThat(userDetailsImpl.getFirstName()).isEqualTo("Admin");
        assertThat(userDetailsImpl.getLastName()).isEqualTo("User");
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testLoadUserByUsername_MultipleUsers() {
        // Given
        User user1 = User.builder()
                .id(1L)
                .email("user1@example.com")
                .firstName("User")
                .lastName("One")
                .password("password1")
                .admin(false)
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("user2@example.com")
                .firstName("User")
                .lastName("Two")
                .password("password2")
                .admin(false)
                .build();

        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(user2));

        // When
        UserDetails details1 = userDetailsService.loadUserByUsername("user1@example.com");
        UserDetails details2 = userDetailsService.loadUserByUsername("user2@example.com");

        // Then
        assertThat(details1).isNotNull();
        assertThat(details2).isNotNull();
        assertThat(((UserDetailsImpl) details1).getId()).isEqualTo(1L);
        assertThat(((UserDetailsImpl) details2).getId()).isEqualTo(2L);
        verify(userRepository).findByEmail("user1@example.com");
        verify(userRepository).findByEmail("user2@example.com");
    }
}

