package ua.zxc.cowbot.helper;

import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.postgresql.entity.ChatEntity;
import ua.zxc.cowbot.postgresql.entity.UserEntity;

import java.util.Collections;

public class UserInitializr {

    private static final Long USER_ID = 1L;
    private static final String USERNAME = "Username";
    private static final String FIRST_NAME = "Firstname";
    private static final ChatEntity CHAT_ENTITY = ChatInitializr.createEntity();

    public static UserDTO createUserDTO() {
        return UserDTO.builder()
                .id(USER_ID)
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .build();
    }

    public static UserDTO createUserDTOWithCustomId(long userId) {
        return UserDTO.builder()
                .id(userId)
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .build();
    }

    public static UserEntity createUserEntity() {
        return UserEntity.builder()
                .id(USER_ID)
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .chats(Collections.singleton(CHAT_ENTITY))
                .build();
    }
}
