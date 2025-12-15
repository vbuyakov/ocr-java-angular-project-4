package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SessionMapper extends EntityMapper<SessionDto, Session> {

    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "description", target = "description")
    Session toEntity(SessionDto sessionDto);

    @Mapping(target = "teacher_id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "description", target = "description")
    SessionDto toDto(Session session);
}
