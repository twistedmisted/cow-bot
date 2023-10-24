package ua.zxc.cowbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.mapper.impl.ChatMapper;
import ua.zxc.cowbot.postgresql.entity.ChatEntity;
import ua.zxc.cowbot.postgresql.repository.ChatRepository;
import ua.zxc.cowbot.service.ChatService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    private final ChatMapper chatMapper;

    public ChatServiceImpl(ChatRepository chatRepository, ChatMapper chatMapper) {
        this.chatRepository = chatRepository;
        this.chatMapper = chatMapper;
    }

    @Override
    public ChatDTO getChatById(Long chatId) {
        log.info("Getting a chat by id: {}", chatId);
        ChatEntity chatEntity = chatRepository.findById(chatId)
                .orElse(null);
        if (isNull(chatEntity)) {
            log.info("Can not to get chat by id: {}", chatId);
            return null;
        }
        log.info("The chat with id {} successfully found", chatId);
        return chatMapper.entityToDto(chatEntity);
    }

    @Override
    public List<ChatDTO> getAllByUsersId(Long userId) {
        log.info("Getting chats by user id: {}", userId);
        Set<ChatEntity> chatEntitiesByUserId = chatRepository.findAllByUsersId(userId);
        if (chatEntitiesByUserId.isEmpty()) {
            log.warn("Can not to find chats by user id: {}", userId);
            return new ArrayList<>();
        }
        log.info("The chats by user id {} were successfully found", userId);
        return chatEntitiesByUserId.stream()
                .map(chatMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChatDTO insertChat(ChatDTO chatDTO) {
        log.info("Inserting a chat: {}", chatDTO);
        if (existsById(chatDTO.getId())) {
            log.info("The chat with id {} already exists", chatDTO.getId());
            return null;
        }
        ChatEntity chatEntity = chatMapper.dtoToEntity(chatDTO);
        chatEntity = chatRepository.save(chatEntity);
        log.info("The chat with id {} successfully inserted", chatEntity.getId());
        return chatMapper.entityToDto(chatEntity);
    }

    @Override
    public ChatDTO updateChat(ChatDTO chatDTO) {
        log.info("Updating a chat with id: {}", chatDTO.getId());
        if (!existsById(chatDTO.getId())) {
            log.info("The chat with id {} does not exist", chatDTO.getId());
            return null;
        }
        ChatEntity chatEntity = chatMapper.dtoToEntity(chatDTO);
        chatEntity = chatRepository.save(chatEntity);
        log.info("The chat with id {} successfully updated", chatDTO.getId());
        return chatMapper.entityToDto(chatEntity);
    }

    @Override
    public boolean existsById(Long id) {
        return chatRepository.existsById(id);
    }
}
