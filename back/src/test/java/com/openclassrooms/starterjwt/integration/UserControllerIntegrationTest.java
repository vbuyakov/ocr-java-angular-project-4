package com.openclassrooms.starterjwt.integration;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private User testUser;
    private String authToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = User.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password(passwordEncoder.encode("password123"))
                .admin(false)
                .build();
        testUser = userRepository.save(testUser);

        // Generate JWT token for authentication
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(testUser.getId())
                .username(testUser.getEmail())
                .firstName(testUser.getFirstName())
                .lastName(testUser.getLastName())
                .admin(testUser.isAdmin())
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        authToken = jwtUtils.generateJwtToken(authentication);
    }

    @Test
    void testFindById_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user/{id}", testUser.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    void testFindById_UserNotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        mockMvc.perform(get("/api/user/{id}", nonExistentId)
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindById_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDelete_Success() throws Exception {
        // Given - Create another user to delete
        User userToDelete = User.builder()
                .email("todelete@example.com")
                .firstName("To")
                .lastName("Delete")
                .password(passwordEncoder.encode("password123"))
                .admin(false)
                .build();
        userToDelete = userRepository.save(userToDelete);

        // Generate token for the user to delete
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(userToDelete.getId())
                .username(userToDelete.getEmail())
                .firstName(userToDelete.getFirstName())
                .lastName(userToDelete.getLastName())
                .admin(userToDelete.isAdmin())
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        String deleteToken = jwtUtils.generateJwtToken(authentication);

        // When & Then
        mockMvc.perform(delete("/api/user/{id}", userToDelete.getId())
                        .header("Authorization", "Bearer " + deleteToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify user was deleted
        assertThat(userRepository.findById(userToDelete.getId())).isEmpty();
    }

    @Test
    void testDelete_Unauthorized_NotOwner() throws Exception {
        // Given - Create another user
        User otherUser = User.builder()
                .email("other@example.com")
                .firstName("Other")
                .lastName("User")
                .password(passwordEncoder.encode("password123"))
                .admin(false)
                .build();
        otherUser = userRepository.save(otherUser);

        // When & Then - Try to delete other user with testUser's token (returns BadRequest, not Unauthorized)
        mockMvc.perform(delete("/api/user/{id}", otherUser.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDelete_Unauthorized_NoToken() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/user/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDelete_UserNotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        mockMvc.perform(delete("/api/user/{id}", nonExistentId)
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

