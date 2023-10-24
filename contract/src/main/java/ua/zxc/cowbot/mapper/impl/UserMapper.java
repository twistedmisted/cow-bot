package ua.zxc.cowbot.mapper.impl;

import org.springframework.stereotype.Component;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.mapper.Mapper;
import ua.zxc.cowbot.postgresql.entity.ChatEntity;
import ua.zxc.cowbot.postgresql.entity.UserEntity;
import ua.zxc.cowbot.postgresql.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper implements Mapper<UserEntity, UserDTO> {

    private final ChatRepository chatRepository;

    public UserMapper(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public UserEntity dtoToEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setFirstName(dto.getFirstName());
        return entity;
    }

    @Override
    public UserDTO entityToDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setFirstName(entity.getFirstName());
        return dto;
    }

    @Override
    public List<UserEntity> dtosToEntities(List<UserDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<UserEntity> entities = new ArrayList<>(dtos.size());
        for (UserDTO dto : dtos) {
            entities.add(dtoToEntity(dto));
        }
        return entities;
    }

    @Override
    public List<UserDTO> entitiesToDtos(List<UserEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<UserDTO> dtos = new ArrayList<>(entities.size());
        for (UserEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }
}
