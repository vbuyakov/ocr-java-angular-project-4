package com.openclassrooms.starterjwt.integration;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.AuthMapper;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.stream.Collectors;

@TestConfiguration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TestConfig {

    // --------------------------
    // AuthMapper Stub
    // --------------------------
    @Bean
    @Primary
    public AuthMapper authMapper() {
        return signupRequest -> User.builder()
                .email(signupRequest.getEmail())
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName())
                .password("")          // deterministic password for tests
                .admin(false)          // tests assume new users are not admin
                .build();
    }

    // --------------------------
    // UserMapper Stub
    // --------------------------
    @Bean
    @Primary
    public UserMapper userMapper() {
        return new UserMapper() {

            @Override
            public User toEntity(UserDto dto) {
                return User.builder()
                        .id(dto.getId())
                        .email(dto.getEmail())
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .password(dto.getPassword())
                        .admin(dto.isAdmin())
                        .createdAt(dto.getCreatedAt())
                        .updatedAt(dto.getUpdatedAt())
                        .build();
            }

            @Override
            public UserDto toDto(User entity) {
                UserDto dto = new UserDto();
                dto.setId(entity.getId());
                dto.setEmail(entity.getEmail());
                dto.setFirstName(entity.getFirstName());
                dto.setLastName(entity.getLastName());
                dto.setPassword(entity.getPassword());
                dto.setAdmin(entity.isAdmin());
                dto.setCreatedAt(entity.getCreatedAt());
                dto.setUpdatedAt(entity.getUpdatedAt());
                return dto;
            }

            @Override
            public List<User> toEntity(List<UserDto> dtoList) {
                return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
            }

            @Override
            public List<UserDto> toDto(List<User> entityList) {
                return entityList.stream().map(this::toDto).collect(Collectors.toList());
            }
        };
    }

    // --------------------------
    // TeacherMapper Stub
    // --------------------------
    @Bean
    @Primary
    public TeacherMapper teacherMapper() {
        return new TeacherMapper() {

            @Override
            public Teacher toEntity(TeacherDto dto) {
                return Teacher.builder()
                        .id(dto.getId())
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .createdAt(dto.getCreatedAt())
                        .updatedAt(dto.getUpdatedAt())
                        .build();
            }

            @Override
            public TeacherDto toDto(Teacher entity) {
                TeacherDto dto = new TeacherDto();
                dto.setId(entity.getId());
                dto.setFirstName(entity.getFirstName());
                dto.setLastName(entity.getLastName());
                dto.setCreatedAt(entity.getCreatedAt());
                dto.setUpdatedAt(entity.getUpdatedAt());
                return dto;
            }

            @Override
            public List<Teacher> toEntity(List<TeacherDto> dtoList) {
                return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
            }

            @Override
            public List<TeacherDto> toDto(List<Teacher> entityList) {
                return entityList.stream().map(this::toDto).collect(Collectors.toList());
            }
        };
    }

    // --------------------------
    // SessionMapper Stub
    // --------------------------
    @Bean
    @Primary
    public SessionMapper sessionMapper() {
        return new SessionMapper() {

            @Override
            public Session toEntity(SessionDto dto) {
                Session session = new Session();
                session.setId(dto.getId());
                session.setName(dto.getName());
                session.setDate(dto.getDate());
                session.setDescription(dto.getDescription());
                session.setCreatedAt(dto.getCreatedAt());
                session.setUpdatedAt(dto.getUpdatedAt());
                return session;
            }

            @Override
            public SessionDto toDto(Session entity) {
                SessionDto dto = new SessionDto();
                dto.setId(entity.getId());
                dto.setName(entity.getName());
                dto.setDate(entity.getDate());
                dto.setDescription(entity.getDescription());
                dto.setCreatedAt(entity.getCreatedAt());
                dto.setUpdatedAt(entity.getUpdatedAt());
                return dto;
            }

            @Override
            public List<Session> toEntity(List<SessionDto> dtoList) {
                return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
            }

            @Override
            public List<SessionDto> toDto(List<Session> entityList) {
                return entityList.stream().map(this::toDto).collect(Collectors.toList());
            }
        };
    }
}
