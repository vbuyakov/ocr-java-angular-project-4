package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        testTeacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    @Test
    void testFindAll_Success() {
        // Given
        Teacher teacher1 = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();
        Teacher teacher2 = Teacher.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .build();
        List<Teacher> teachers = Arrays.asList(teacher1, teacher2);

        when(teacherRepository.findAll()).thenReturn(teachers);

        // When
        List<Teacher> result = teacherService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        assertThat(result.get(0).getLastName()).isEqualTo("Doe");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getFirstName()).isEqualTo("Jane");
        assertThat(result.get(1).getLastName()).isEqualTo("Smith");
        verify(teacherRepository).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        // Given
        when(teacherRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Teacher> result = teacherService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(teacherRepository).findAll();
    }

    @Test
    void testFindById_Success() {
        // Given
        Long teacherId = 1L;
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(testTeacher));

        // When
        Teacher result = teacherService.findById(teacherId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(teacherId);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        verify(teacherRepository).findById(teacherId);
    }

    @Test
    void testFindById_NotFound() {
        // Given
        Long teacherId = 999L;
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> teacherService.findById(teacherId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Teacher not found");
        verify(teacherRepository).findById(teacherId);
    }

    @Test
    void testFindById_MultipleTeachers() {
        // Given
        Teacher teacher1 = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();
        Teacher teacher2 = Teacher.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .build();
        Teacher teacher3 = Teacher.builder()
                .id(3L)
                .firstName("Bob")
                .lastName("Johnson")
                .build();

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));
        when(teacherRepository.findById(2L)).thenReturn(Optional.of(teacher2));
        when(teacherRepository.findById(3L)).thenReturn(Optional.of(teacher3));

        // When
        Teacher result1 = teacherService.findById(1L);
        Teacher result2 = teacherService.findById(2L);
        Teacher result3 = teacherService.findById(3L);

        // Then
        assertThat(result1.getId()).isEqualTo(1L);
        assertThat(result1.getFirstName()).isEqualTo("John");
        assertThat(result2.getId()).isEqualTo(2L);
        assertThat(result2.getFirstName()).isEqualTo("Jane");
        assertThat(result3.getId()).isEqualTo(3L);
        assertThat(result3.getFirstName()).isEqualTo("Bob");
        verify(teacherRepository).findById(1L);
        verify(teacherRepository).findById(2L);
        verify(teacherRepository).findById(3L);
    }
}

