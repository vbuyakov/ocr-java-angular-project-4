package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TeacherControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Teacher teacher1;
    private Teacher teacher2;

    @BeforeEach
    void setUp() {
        // Delete in correct order to avoid foreign key constraints
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();

        teacher1 = Teacher.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        teacher1 = teacherRepository.save(teacher1);

        teacher2 = Teacher.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();
        teacher2 = teacherRepository.save(teacher2);
    }

    @Test
    @WithMockUser
    void testFindAll_ShouldReturnAllTeachers() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(teacher1.getId()))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].id").value(teacher2.getId()))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"));
    }

    @Test
    @WithMockUser
    void testFindAll_WhenNoTeachers_ShouldReturnEmptyList() throws Exception {
        // Given - Delete in correct order to avoid foreign key constraints
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();

        // When & Then
        mockMvc.perform(get("/api/teacher")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    void testFindById_ShouldReturnTeacher() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/teacher/{id}", teacher1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(teacher1.getId()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser
    void testFindById_WhenTeacherNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        mockMvc.perform(get("/api/teacher/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testFindById_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/teacher/{id}", "invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

