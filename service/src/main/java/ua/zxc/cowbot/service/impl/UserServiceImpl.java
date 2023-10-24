package ua.zxc.cowbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.mapper.impl.UserMapper;
import ua.zxc.cowbot.postgresql.entity.UserEntity;
import ua.zxc.cowbot.postgresql.repository.UserRepository;
import ua.zxc.cowbot.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO getUserById(Long userId) {
        log.info("Getting user by id: {}", userId);
        UserEntity userEntity = userRepository.findById(userId)
                .orElse(null);
        if (isNull(userEntity)) {
            log.warn("Can not to get user by id: {}", userId);
            return null;
        }
        log.info("The user with id {} successfully found", userId);
        return userMapper.entityToDto(userEntity);
    }

    @Override
    public List<UserDTO> getAllByChatId(Long chatId) {
        log.info("Getting all users for chat with id: {}", chatId);
        Set<UserEntity> usersEntitiesByChatId = userRepository.findAllByChatsId(chatId);
        if (usersEntitiesByChatId.isEmpty()) {
            log.info("Can not to find users by chat id: {}", chatId);
            return new ArrayList<>();
        }
        log.info("The users by chat id {} were successfully found", chatId);
        return usersEntitiesByChatId.stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getAllByChatId(Long chatId, Integer pageNum, Integer pageSize) {
        log.info("Getting all users for chat with id: {} and pageNum: {} and pageSize: {}", chatId, pageNum, pageSize);
        Page<UserEntity> usersEntitiesPageByChatId =
                userRepository.findAllByChatsId(PageRequest.of(pageNum, pageSize), chatId);
        if (!usersEntitiesPageByChatId.hasContent()) {
            log.info("Can not to find users by chat id: {} and page parameters(pageNum = {} and pageSize = {})",
                    chatId, pageNum, pageSize);
            return new ArrayList<>();
        }
        log.info("The users by chat id {} and page parameters(pageNum = {} and pageSize = {}) were successfully found",
                chatId, pageNum, pageSize);
        return usersEntitiesPageByChatId.getContent()
                .stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO insertUser(UserDTO userDTO) {
        log.info("Inserting user: {}", userDTO);
        if (existsUserById(userDTO.getId())) {
            log.warn("The user with id {} already exists", userDTO.getId());
            return null;
        }
        UserEntity userEntity = userMapper.dtoToEntity(userDTO);
        userEntity = userRepository.save(userEntity);
        log.info("The user with id {} was successfully saved", userDTO.getId());
        return userMapper.entityToDto(userEntity);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        log.info("Updating user with id: {}", userDTO.getId());
        if (!existsUserById(userDTO.getId())) {
            log.warn("The user with id {} does not exist", userDTO.getId());
            return null;
        }
        UserEntity userEntity = userMapper.dtoToEntity(userDTO);
        userEntity = userRepository.save(userEntity);
        log.info("The user with id {} was successfully updated", userDTO.getId());
        return userMapper.entityToDto(userEntity);
    }

    @Override
    public boolean existsUserById(Long userId) {
        return userRepository.existsById(userId);
    }
}
