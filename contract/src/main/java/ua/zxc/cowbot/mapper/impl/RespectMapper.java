package ua.zxc.cowbot.mapper.impl;

import org.springframework.stereotype.Component;
import ua.zxc.cowbot.dto.RespectDTO;
import ua.zxc.cowbot.mapper.Mapper;
import ua.zxc.cowbot.postgresql.entity.RespectEntity;
import ua.zxc.cowbot.postgresql.repository.ChatRepository;
import ua.zxc.cowbot.postgresql.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static ua.zxc.cowbot.utils.Constants.RESPECT_LIST_TITLE;

@Component
public class RespectMapper implements Mapper<RespectEntity, RespectDTO> {

    private final UserRepository userRepository;

    private final ChatRepository chatRepository;

    private final UserMapper userMapper;

    private final ChatMapper chatMapper;

    public RespectMapper(UserRepository userRepository, ChatRepository chatRepository, UserMapper userMapper,
                         ChatMapper chatMapper) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.userMapper = userMapper;
        this.chatMapper = chatMapper;
    }

    @Override
    public RespectEntity dtoToEntity(RespectDTO dto) {
        if (dto == null) {
            return null;
        }
        RespectEntity entity = new RespectEntity();
        entity.setUser(userRepository.findById(dto.getUserId()).get());
        entity.setChat(chatRepository.findById(dto.getChatId()).get());
        entity.setNumberThisMonth(dto.getNumberThisMonth());
        entity.setNumberPrevMonth(dto.getNumberPrevMonth());
        entity.setTotalNumber(dto.getTotalNumber());
        return entity;
    }

    public String dtoToString(RespectDTO dto, int number) {
        if (dto == null) {
            return "";
        }
        return "<b>" +
                number +
                ". " +
                dto.getUser().getFirstName() +
                ":</b> " +
                "<i>" +
                dto.getNumberThisMonth() +
                " впод.</i>" +
                System.lineSeparator();
    }

    @Override
    public RespectDTO entityToDto(RespectEntity entity) {
        if (entity == null) {
            return null;
        }
        RespectDTO dto = new RespectDTO();
        dto.setUserId(entity.getId().getUserId());
        dto.setChatId(entity.getId().getChatId());
        dto.setNumberThisMonth(entity.getNumberThisMonth());
        dto.setNumberPrevMonth(entity.getNumberPrevMonth());
        dto.setTotalNumber(entity.getTotalNumber());
        dto.setUser(userMapper.entityToDto(entity.getUser()));
        dto.setChat(chatMapper.entityToDto(entity.getChat()));
        return dto;
    }

    @Override
    public List<RespectEntity> dtosToEntities(List<RespectDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<RespectEntity> entities = new ArrayList<>();
        for (RespectDTO dto : dtos) {
            entities.add(dtoToEntity(dto));
        }
        return entities;
    }

    @Override
    public List<RespectDTO> entitiesToDtos(List<RespectEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<RespectDTO> dtos = new ArrayList<>();
        for (RespectEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }

    public String dtosToString(List<RespectDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(RESPECT_LIST_TITLE);
        sb.append(System.lineSeparator());
        int number = 1;
        for (RespectDTO dto : dtos) {
            sb.append(dtoToString(dto, number));
            number++;
        }
        return sb.toString();
    }
}
