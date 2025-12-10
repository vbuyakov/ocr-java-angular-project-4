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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SessionMapper sessionMapper;
    private final TeacherService teacherService;

    public Session create(Session session) {
        return sessionRepository.save(session);
    }

    public Session toEntity(SessionDto sessionDto) {
        Session session = sessionMapper.toEntity(sessionDto);
        
        // Handle teacher lookup
        if (sessionDto.getTeacher_id() != null) {
            Teacher teacher = teacherService.findById(sessionDto.getTeacher_id());
            session.setTeacher(teacher);
        }
        
        // Handle users lookup
        if (sessionDto.getUsers() != null && !sessionDto.getUsers().isEmpty()) {
            List<User> users = sessionDto.getUsers().stream()
                    .map(userId -> userRepository.findById(userId)
                            .orElseThrow(() -> new NotFoundException("User not found with id: " + userId)))
                    .collect(Collectors.toList());
            session.setUsers(users);
        } else {
            session.setUsers(Collections.emptyList());
        }
        
        return session;
    }

    public SessionDto toDto(Session session) {
        SessionDto sessionDto = sessionMapper.toDto(session);
        
        // Handle teacher_id
        if (session.getTeacher() != null) {
            sessionDto.setTeacher_id(session.getTeacher().getId());
        }
        
        // Handle users
        if (session.getUsers() != null && !session.getUsers().isEmpty()) {
            List<Long> userIds = session.getUsers().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            sessionDto.setUsers(userIds);
        } else {
            sessionDto.setUsers(Collections.emptyList());
        }
        
        return sessionDto;
    }

    public void delete(Long id) {
        getById(id); // Verify session exists
        sessionRepository.deleteById(id);
    }

    public List<Session> findAll() {
        return sessionRepository.findAll();
    }

    public Session getById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Session not found"));
    }

    public Session update(Long id, Session session) {
        getById(id); // Verify session exists
        session.setId(id);
        return sessionRepository.save(session);
    }

    public void participate(Long id, Long userId) {
        Session session = getById(id);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean alreadyParticipate = session.getUsers().stream()
                .anyMatch(u -> u.getId().equals(userId));
        if (alreadyParticipate) {
            throw new BadRequestException("User is already participating in this session");
        }

        session.getUsers().add(user);
        sessionRepository.save(session);
    }

    public void noLongerParticipate(Long id, Long userId) {
        Session session = getById(id);

        boolean alreadyParticipate = session.getUsers().stream()
                .anyMatch(u -> u.getId().equals(userId));
        if (!alreadyParticipate) {
            throw new BadRequestException("User is not participating in this session");
        }

        session.setUsers(session.getUsers().stream()
                .filter(user -> !user.getId().equals(userId))
                .collect(Collectors.toList()));

        sessionRepository.save(session);
    }
}
