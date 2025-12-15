package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.services.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherMapper teacherMapper;
    private final TeacherService teacherService;

    @GetMapping("/{id}")
    public ResponseEntity<TeacherDto> findById(@PathVariable("id") String id) {
        Long teacherId = Long.parseLong(id);
        TeacherDto teacherDto = teacherMapper.toDto(teacherService.findById(teacherId));
        return ResponseEntity.ok(teacherDto);
    }

    @GetMapping
    public ResponseEntity<List<TeacherDto>> findAll() {
        List<TeacherDto> teacherDtos = teacherMapper.toDto(teacherService.findAll());
        return ResponseEntity.ok(teacherDtos);
    }
}
