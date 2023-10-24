package ua.zxc.cowbot.mapper.impl;

import org.springframework.stereotype.Component;
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.mapper.Mapper;
import ua.zxc.cowbot.postgresql.entity.ChatEntity;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChatMapper implements Mapper<ChatEntity, ChatDTO> {

    @Override
    public ChatEntity dtoToEntity(ChatDTO dto) {
        if (dto == null) {
            return null;
        }
        ChatEntity entity = new ChatEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setGroupName(dto.getGroupName());
        return entity;
    }

    @Override
    public ChatDTO entityToDto(ChatEntity entity) {
        if (entity == null) {
            return null;
        }
        ChatDTO dto = new ChatDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setGroupName(entity.getGroupName());
        return dto;
    }

    @Override
    public List<ChatEntity> dtosToEntities(List<ChatDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<ChatEntity> entities = new ArrayList<>(dtos.size());
        for (ChatDTO dto : dtos) {
            entities.add(dtoToEntity(dto));
        }
        return entities;
    }

    @Override
    public List<ChatDTO> entitiesToDtos(List<ChatEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<ChatDTO> dtos = new ArrayList<>(entities.size());
        for (ChatEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }
}
