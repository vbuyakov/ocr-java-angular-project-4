package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.mapper.AuthMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserDetailsImpl userDetails;
    private LoginRequest loginRequest;
    private SignupRequest signupRequest;

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

        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("Jane");
        signupRequest.setLastName("Smith");
    }

    @Test
    void testAuthenticateUser_Success() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("test-jwt-token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        JwtResponse response = authService.authenticateUser(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("test-jwt-token");
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("test@example.com");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getAdmin()).isFalse();
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authentication);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testAuthenticateUser_InvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When/Then
        assertThatThrownBy(() -> authService.authenticateUser(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, never()).generateJwtToken(any());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void testAuthenticateUser_UserNotFoundAfterAuthentication() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("test-jwt-token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> authService.authenticateUser(loginRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("User not found after authentication");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authentication);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testAuthenticateUser_AdminUser() {
        // Given
        User adminUser = User.builder()
                .id(2L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("encodedPassword")
                .admin(true)
                .build();

        UserDetailsImpl adminDetails = UserDetailsImpl.builder()
                .id(2L)
                .username("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .admin(true)
                .build();

        LoginRequest adminLoginRequest = new LoginRequest();
        adminLoginRequest.setEmail("admin@example.com");
        adminLoginRequest.setPassword("password123");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("admin-jwt-token");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // When
        JwtResponse response = authService.authenticateUser(adminLoginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAdmin()).isTrue();
        assertThat(response.getToken()).isEqualTo("admin-jwt-token");
    }

    @Test
    void testRegisterUser_Success() {
        // Given
        User newUser = User.builder()
                .email("newuser@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("") // Will be set by service
                .admin(false)
                .build();

        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(authMapper.toUser(signupRequest)).thenReturn(newUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        MessageResponse response = authService.registerUser(signupRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("User registered successfully!");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(authMapper).toUser(signupRequest);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyTaken() {
        // Given
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> authService.registerUser(signupRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email is already taken");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(authMapper, never()).toUser(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_MapperReturnsNull() {
        // Given
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(authMapper.toUser(signupRequest)).thenReturn(null);

        // When/Then
        assertThatThrownBy(() -> authService.registerUser(signupRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Failed to create user");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(authMapper).toUser(signupRequest);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}

