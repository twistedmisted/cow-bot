package ua.zxc.cowbot.mapper.impl;

import org.springframework.stereotype.Component;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.mapper.Mapper;
import ua.zxc.cowbot.postgresql.entity.LessonEntity;
import ua.zxc.cowbot.postgresql.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class LessonMapper implements Mapper<LessonEntity, LessonDTO> {

    private final ChatRepository chatRepository;

    public LessonMapper(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public LessonEntity dtoToEntity(LessonDTO dto) {
        if (dto == null) {
            return null;
        }
        LessonEntity entity = new LessonEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setFullTeacherName(dto.getFullTeacherName());
        entity.setUrl(dto.getUrl());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        // TODO: think about exception | maybe exception is better think about this
        entity.setChat(chatRepository.findById(dto.getChatId()).get());
        return entity;
    }

    @Override
    public LessonDTO entityToDto(LessonEntity entity) {
        if (entity == null) {
            return null;
        }
        LessonDTO dto = new LessonDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setFullTeacherName(entity.getFullTeacherName());
        dto.setUrl(entity.getUrl());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setChatId(entity.getChat().getId());
        return dto;
    }

    @Override
    public List<LessonEntity> dtosToEntities(List<LessonDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<LessonEntity> entities = new ArrayList<>(dtos.size());
        for (LessonDTO dto : dtos) {
            entities.add(dtoToEntity(dto));
        }
        return entities;
    }

    @Override
    public List<LessonDTO> entitiesToDtos(List<LessonEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<LessonDTO> dtos = new ArrayList<>(entities.size());
        for (LessonEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }
}
