package ua.zxc.cowbot.helper;

import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.postgresql.entity.ChatEntity;

public class ChatInitializr {

    private static final long ID = 1L;
    private static final String NAME = "Test name";
    private static final String GROUP_NAME = "XX-00";

    public static ChatDTO createDTOWithCustomId(long chatId) {
        return ChatDTO.builder()
                .id(chatId)
                .name(NAME)
                .groupName(GROUP_NAME)
                .build();
    }

    public static ChatDTO createDTO() {
        return ChatDTO.builder()
                .id(ID)
                .name(NAME)
                .groupName(GROUP_NAME)
                .build();
    }

    public static ChatDTO createDTOWithoutGroupName() {
        return ChatDTO.builder()
                .id(ID)
                .name(NAME)
                .build();
    }

    public static ChatEntity createEntity() {
        return ChatEntity.builder()
                .id(ID)
                .name(NAME)
                .groupName(GROUP_NAME)
                .build();
    }
}
