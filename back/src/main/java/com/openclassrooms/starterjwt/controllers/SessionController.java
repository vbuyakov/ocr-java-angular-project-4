package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.services.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> findById(@PathVariable("id") String id) {
        Long sessionId = Long.parseLong(id);
        SessionDto sessionDto = sessionService.toDto(sessionService.getById(sessionId));
        return ResponseEntity.ok(sessionDto);
    }

    @GetMapping
    public ResponseEntity<List<SessionDto>> findAll() {
        List<SessionDto> sessionDtos = sessionService.findAll().stream()
                .map(sessionService::toDto)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(sessionDtos);
    }

    @PostMapping
    public ResponseEntity<SessionDto> create(@Valid @RequestBody SessionDto sessionDto) {
        SessionDto createdSession = sessionService.toDto(
                sessionService.create(sessionService.toEntity(sessionDto))
        );
        return ResponseEntity.ok(createdSession);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionDto> update(@PathVariable("id") String id, @Valid @RequestBody SessionDto sessionDto) {
        Long sessionId = Long.parseLong(id);
        SessionDto updatedSession = sessionService.toDto(
                sessionService.update(sessionId, sessionService.toEntity(sessionDto))
        );
        return ResponseEntity.ok(updatedSession);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        Long sessionId = Long.parseLong(id);
        sessionService.delete(sessionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/participate/{userId}")
    public ResponseEntity<Void> participate(@PathVariable("id") String id, @PathVariable("userId") String userId) {
        Long sessionId = Long.parseLong(id);
        Long user = Long.parseLong(userId);
        sessionService.participate(sessionId, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/participate/{userId}")
    public ResponseEntity<Void> noLongerParticipate(@PathVariable("id") String id, @PathVariable("userId") String userId) {
        Long sessionId = Long.parseLong(id);
        Long user = Long.parseLong(userId);
        sessionService.noLongerParticipate(sessionId, user);
        return ResponseEntity.ok().build();
    }
}
