package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionMapper sessionMapper;

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private SessionService sessionService;

    private Session testSession;
    private Teacher testTeacher;
    private User testUser;

    @BeforeEach
    void setUp() {
        testTeacher = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        testUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("encodedPassword")
                .admin(false)
                .build();

        testSession = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .date(new Date())
                .description("Test session")
                .teacher(testTeacher)
                .users(new ArrayList<>())
                .build();
    }

    @Test
    void testCreate_Success() {
        // Given
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        // When
        Session result = sessionService.create(testSession);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(sessionRepository).save(testSession);
    }

    @Test
    void testGetById_Success() {
        // Given
        Long sessionId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));

        // When
        Session result = sessionService.getById(sessionId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(sessionId);
        verify(sessionRepository).findById(sessionId);
    }

    @Test
    void testGetById_NotFound() {
        // Given
        Long sessionId = 999L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> sessionService.getById(sessionId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Session not found");
        verify(sessionRepository).findById(sessionId);
    }

    @Test
    void testFindAll_Success() {
        // Given
        Session session1 = Session.builder().id(1L).name("Session 1").build();
        Session session2 = Session.builder().id(2L).name("Session 2").build();
        List<Session> sessions = Arrays.asList(session1, session2);

        when(sessionRepository.findAll()).thenReturn(sessions);

        // When
        List<Session> result = sessionService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(sessionRepository).findAll();
    }

    @Test
    void testDelete_Success() {
        // Given
        Long sessionId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        doNothing().when(sessionRepository).deleteById(sessionId);

        // When
        sessionService.delete(sessionId);

        // Then
        verify(sessionRepository).findById(sessionId);
        verify(sessionRepository).deleteById(sessionId);
    }

    @Test
    void testUpdate_Success() {
        // Given
        Long sessionId = 1L;
        Session updatedSession = Session.builder()
                .id(sessionId)
                .name("Updated Session")
                .date(new Date())
                .description("Updated description")
                .build();

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(Session.class))).thenReturn(updatedSession);

        // When
        Session result = sessionService.update(sessionId, updatedSession);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(sessionId);
        assertThat(result.getName()).isEqualTo("Updated Session");
        verify(sessionRepository).findById(sessionId);
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void testParticipate_Success() {
        // Given
        Long sessionId = 1L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        // When
        sessionService.participate(sessionId, userId);

        // Then
        verify(sessionRepository).findById(sessionId);
        verify(userRepository).findById(userId);
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void testParticipate_UserAlreadyParticipating() {
        // Given
        Long sessionId = 1L;
        Long userId = 1L;
        testSession.getUsers().add(testUser);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When/Then
        assertThatThrownBy(() -> sessionService.participate(sessionId, userId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already participating");
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void testNoLongerParticipate_Success() {
        // Given
        Long sessionId = 1L;
        Long userId = 1L;
        testSession.getUsers().add(testUser);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        // When
        sessionService.noLongerParticipate(sessionId, userId);

        // Then
        verify(sessionRepository).findById(sessionId);
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void testNoLongerParticipate_UserNotParticipating() {
        // Given
        Long sessionId = 1L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));

        // When/Then
        assertThatThrownBy(() -> sessionService.noLongerParticipate(sessionId, userId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not participating");
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void testToEntity_Success_WithTeacherAndUsers() {
        // Given
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Test Session");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Test description");
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(Arrays.asList(1L, 2L));

        Session session = Session.builder()
                .name("Test Session")
                .date(sessionDto.getDate())
                .description("Test description")
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("user2@example.com")
                .firstName("User")
                .lastName("Two")
                .password("encodedPassword")
                .admin(false)
                .build();

        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(teacherService.findById(1L)).thenReturn(testTeacher);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        // When
        Session result = sessionService.toEntity(sessionDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTeacher()).isEqualTo(testTeacher);
        assertThat(result.getUsers()).hasSize(2);
        assertThat(result.getUsers()).contains(testUser, user2);
        verify(sessionMapper).toEntity(sessionDto);
        verify(teacherService).findById(1L);
        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
    }

    @Test
    void testToEntity_Success_WithNullTeacherId() {
        // Given
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Test Session");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Test description");
        sessionDto.setTeacher_id(null);
        sessionDto.setUsers(Arrays.asList(1L));

        Session session = Session.builder()
                .name("Test Session")
                .date(sessionDto.getDate())
                .description("Test description")
                .build();

        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Session result = sessionService.toEntity(sessionDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTeacher()).isNull();
        assertThat(result.getUsers()).hasSize(1);
        verify(sessionMapper).toEntity(sessionDto);
        verify(teacherService, never()).findById(any());
        verify(userRepository).findById(1L);
    }

    @Test
    void testToEntity_Success_WithEmptyUsersList() {
        // Given
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Test Session");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Test description");
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(Collections.emptyList());

        Session session = Session.builder()
                .name("Test Session")
                .date(sessionDto.getDate())
                .description("Test description")
                .build();

        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(teacherService.findById(1L)).thenReturn(testTeacher);

        // When
        Session result = sessionService.toEntity(sessionDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTeacher()).isEqualTo(testTeacher);
        assertThat(result.getUsers()).isEmpty();
        verify(sessionMapper).toEntity(sessionDto);
        verify(teacherService).findById(1L);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void testToEntity_UserNotFound() {
        // Given
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Test Session");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Test description");
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(Arrays.asList(999L));

        Session session = Session.builder()
                .name("Test Session")
                .date(sessionDto.getDate())
                .description("Test description")
                .build();

        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(teacherService.findById(1L)).thenReturn(testTeacher);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> sessionService.toEntity(sessionDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found with id: 999");
        verify(sessionMapper).toEntity(sessionDto);
        verify(teacherService).findById(1L);
        verify(userRepository).findById(999L);
    }

    @Test
    void testToDto_Success_WithTeacherAndUsers() {
        // Given
        testSession.getUsers().add(testUser);
        User user2 = User.builder()
                .id(2L)
                .email("user2@example.com")
                .firstName("User")
                .lastName("Two")
                .password("encodedPassword")
                .admin(false)
                .build();
        testSession.getUsers().add(user2);

        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Yoga Session");
        sessionDto.setDate(testSession.getDate());
        sessionDto.setDescription("Test session");

        when(sessionMapper.toDto(testSession)).thenReturn(sessionDto);

        // When
        SessionDto result = sessionService.toDto(testSession);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTeacher_id()).isEqualTo(1L);
        assertThat(result.getUsers()).hasSize(2);
        assertThat(result.getUsers()).contains(1L, 2L);
        verify(sessionMapper).toDto(testSession);
    }

    @Test
    void testToDto_Success_WithNullTeacher() {
        // Given
        Session sessionWithoutTeacher = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .date(new Date())
                .description("Test session")
                .teacher(null)
                .users(new ArrayList<>())
                .build();

        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Yoga Session");
        sessionDto.setDate(sessionWithoutTeacher.getDate());
        sessionDto.setDescription("Test session");

        when(sessionMapper.toDto(sessionWithoutTeacher)).thenReturn(sessionDto);

        // When
        SessionDto result = sessionService.toDto(sessionWithoutTeacher);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTeacher_id()).isNull();
        assertThat(result.getUsers()).isEmpty();
        verify(sessionMapper).toDto(sessionWithoutTeacher);
    }

    @Test
    void testToDto_Success_WithEmptyUsersList() {
        // Given
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Yoga Session");
        sessionDto.setDate(testSession.getDate());
        sessionDto.setDescription("Test session");

        when(sessionMapper.toDto(testSession)).thenReturn(sessionDto);

        // When
        SessionDto result = sessionService.toDto(testSession);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTeacher_id()).isEqualTo(1L);
        assertThat(result.getUsers()).isEmpty();
        verify(sessionMapper).toDto(testSession);
    }
}

