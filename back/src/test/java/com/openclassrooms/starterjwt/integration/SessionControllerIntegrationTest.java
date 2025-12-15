package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private Teacher testTeacher;
    private User testUser;
    private Session testSession;
    private String authToken;
    private String testSessionName;

    @BeforeEach
    void setUp() {
        // Delete in correct order to avoid foreign key constraints
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();

        testTeacher = Teacher.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        testTeacher = teacherRepository.save(testTeacher);

        testUser = User.builder()
                .email("test@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password(passwordEncoder.encode("password123"))
                .admin(false)
                .build();
        testUser = userRepository.save(testUser);

        long uniqueId = System.currentTimeMillis() % 1000000; // Keep it short
        testSessionName = "Yoga " + uniqueId;
        testSession = Session.builder()
                .name(testSessionName)
                .date(new Date())
                .description("Test session description")
                .teacher(testTeacher)
                .users(new ArrayList<>())
                .build();
        testSession = sessionRepository.save(testSession);

        // Generate JWT token
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
    void testFindAll_Success() throws Exception {
        // When & Then - testSession is already created in setUp
        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(testSession.getId()));
    }

    @Test
    void testFindById_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/session/{id}", testSession.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testSession.getId()))
                .andExpect(jsonPath("$.name").value(testSessionName))
                .andExpect(jsonPath("$.description").value("Test session description"))
                .andExpect(jsonPath("$.teacher_id").value(testTeacher.getId()));
    }

    @Test
    void testFindById_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/session/{id}", 999L)
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate_Success() throws Exception {
        // Given - Create a new teacher for this session (OneToOne constraint requires unique teacher_id)
        Teacher newTeacher = Teacher.builder()
                .firstName("Jane")
                .lastName("Teacher")
                .build();
        newTeacher = teacherRepository.save(newTeacher);

        long uniqueId = (System.currentTimeMillis() + 2000) % 1000000;
        String sessionName = "New " + uniqueId;
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName(sessionName);
        sessionDto.setDate(new Date());
        sessionDto.setDescription("New session description");
        sessionDto.setTeacher_id(newTeacher.getId()); // Use new teacher to avoid OneToOne constraint violation
        sessionDto.setUsers(null); // null is acceptable, will be converted to empty list

        // When & Then
        mockMvc.perform(post("/api/session")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(sessionName))
                .andExpect(jsonPath("$.description").value("New session description"))
                .andExpect(jsonPath("$.teacher_id").value(newTeacher.getId()));

        // Verify session was created
        List<Session> sessions = sessionRepository.findAll();
        assertThat(sessions).hasSize(2);
    }

    @Test
    void testUpdate_Success() throws Exception {
        // Given
        long uniqueId = (System.currentTimeMillis() + 3000) % 1000000;
        String updatedName = "Updated " + uniqueId;
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName(updatedName);
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Updated description");
        sessionDto.setTeacher_id(testTeacher.getId());
        sessionDto.setUsers(new ArrayList<>());

        // When & Then
        mockMvc.perform(put("/api/session/{id}", testSession.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(updatedName))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void testDelete_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/session/{id}", testSession.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify session was deleted
        assertThat(sessionRepository.findById(testSession.getId())).isEmpty();
    }

    @Test
    void testParticipate_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", testSession.getId(), testUser.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify user was added to session
        Session updatedSession = sessionRepository.findById(testSession.getId()).orElseThrow();
        assertThat(updatedSession.getUsers()).hasSize(1);
        assertThat(updatedSession.getUsers().get(0).getId()).isEqualTo(testUser.getId());
    }

    @Test
    void testParticipate_UserAlreadyParticipating() throws Exception {
        // Given - Add user to session first
        testSession.getUsers().add(testUser);
        sessionRepository.save(testSession);

        // When & Then
        mockMvc.perform(post("/api/session/{id}/participate/{userId}", testSession.getId(), testUser.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testNoLongerParticipate_Success() throws Exception {
        // Given - Add user to session first
        testSession.getUsers().add(testUser);
        sessionRepository.save(testSession);

        // When & Then
        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", testSession.getId(), testUser.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify user was removed from session
        Session updatedSession = sessionRepository.findById(testSession.getId()).orElseThrow();
        assertThat(updatedSession.getUsers()).isEmpty();
    }

    @Test
    void testNoLongerParticipate_UserNotParticipating() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/session/{id}/participate/{userId}", testSession.getId(), testUser.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreate_Unauthorized() throws Exception {
        // Given
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("New Session");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("New session description");
        sessionDto.setTeacher_id(testTeacher.getId());

        // When & Then
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isUnauthorized());
    }
}

