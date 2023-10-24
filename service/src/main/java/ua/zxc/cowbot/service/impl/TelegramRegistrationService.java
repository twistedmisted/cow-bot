package ua.zxc.cowbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.mapper.impl.ChatMapper;
import ua.zxc.cowbot.mapper.impl.UserMapper;
import ua.zxc.cowbot.postgresql.entity.ChatEntity;
import ua.zxc.cowbot.postgresql.entity.UserEntity;
import ua.zxc.cowbot.postgresql.repository.ChatRepository;
import ua.zxc.cowbot.postgresql.repository.UserRepository;
import ua.zxc.cowbot.service.RegistrationService;

import javax.transaction.Transactional;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class TelegramRegistrationService implements RegistrationService {

    private final UserRepository userRepository;

    private final ChatRepository chatRepository;

    private final UserMapper userMapper;

    private final ChatMapper chatMapper;

    public TelegramRegistrationService(UserRepository userRepository, ChatRepository chatRepository,
                                       UserMapper userMapper, ChatMapper chatMapper) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.userMapper = userMapper;
        this.chatMapper = chatMapper;
    }

    @Override
    @Transactional
    public boolean registration(UserDTO user, ChatDTO chat) {
        Long userId = user.getId();
        Long chatId = chat.getId();
        if (isUserRegisteredInChat(userId, chatId)) {
            log.debug("The user already registered in this chat: [userId = '{}' and chatId = '{}']",
                    user.getId(), chat.getId());
            return false;
        }
        ChatEntity chatEntity = getChatEntityById(chatId);
        if (isNull(chatEntity)) {
            log.debug("Inserting new chat: [chat = '{}']", chat);
            chatEntity = insertChat(chat);
            log.info("The new chat successful created: [chatId = '{}']", chatEntity.getId());
        }
        return registerUserToChat(user, userId, chatEntity);
    }

    private boolean isUserRegisteredInChat(Long userId, Long chatId) {
        return userRepository.existsByIdAndChatsId(userId, chatId);
    }

    private ChatEntity getChatEntityById(Long chatId) {
        return chatRepository.findById(chatId).orElse(null);
    }

    private ChatEntity insertChat(ChatDTO chat) {
        try {
            return chatRepository.save(chatMapper.dtoToEntity(chat));
        } catch (RuntimeException e) {
            log.warn("Cannot insert chat with id: [chatId = '{}']", chat.getId(), e);
            throw new RuntimeException("Cannot insert chat with id: [chatId = '" + chat.getId() + "']");
        }
    }

    private boolean registerUserToChat(UserDTO user, Long userId, ChatEntity chatEntity) {
        if (isUserRegistered(userId)) {
            log.debug("Updating existing user and adding new chat to him");
            return updateUserAndAddChat(user, chatEntity);
        }
        log.debug("Inserting new user with chat");
        return insertNewUser(user, chatEntity);
    }

    private boolean isUserRegistered(Long userId) {
        return userRepository.existsById(userId);
    }

    private boolean updateUserAndAddChat(UserDTO user, ChatEntity chatEntity) {
        UserEntity userEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Cannot find user by id: [userId = '" + user.getId() + "']"));
        updateUserInformation(userEntity, user);
        userEntity.addChat(chatEntity);
        return true;
    }

    private void updateUserInformation(UserEntity userEntity, UserDTO user) {
        userEntity.setUsername(user.getUsername());
        userEntity.setFirstName(user.getFirstName());
    }

    private boolean insertNewUser(UserDTO user, ChatEntity chatEntity) {
        UserEntity userEntity = userMapper.dtoToEntity(user);
        userEntity.addChat(chatEntity);
        insertUser(userEntity);
        return true;
    }

    private void insertUser(UserEntity userEntity) {
        try {
            userRepository.save(userEntity);
        } catch (RuntimeException e) {
            log.warn("Cannot insert user with id: [userId = '{}']", userEntity.getId(), e);
            throw new RuntimeException("Cannot insert chat with id: [userId = '" + userEntity.getId() + "']");
        }
    }
}
