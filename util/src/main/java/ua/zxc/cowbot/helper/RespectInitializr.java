package ua.zxc.cowbot.helper;

import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.dto.RespectDTO;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.postgresql.entity.ChatEntity;
import ua.zxc.cowbot.postgresql.entity.RespectEntity;
import ua.zxc.cowbot.postgresql.entity.UserEntity;

public class RespectInitializr {

    private static final UserEntity USER_ENTITY = UserInitializr.createUserEntity();
    private static final UserDTO USER_DTO = UserInitializr.createUserDTO();
    private static final ChatEntity CHAT_ENTITY = ChatInitializr.createEntity();
    private static final ChatDTO CHAT_DTO = ChatInitializr.createDTO();
    private static final Integer NUMBER_THIS_MONTH = 10;
    private static final Integer NUMBER_PREV_MONTH = 20;
    private static final Integer TOTAL_NUMBER = 30;

    public static RespectDTO createDTO() {
        return RespectDTO.builder()
                .userId(USER_DTO.getId())
                .chatId(CHAT_DTO.getId())
                .numberThisMonth(NUMBER_THIS_MONTH)
                .numberPrevMonth(NUMBER_PREV_MONTH)
                .totalNumber(TOTAL_NUMBER)
                .user(USER_DTO)
                .chat(CHAT_DTO)
                .build();
    }

    public static RespectEntity createEntity() {
        return RespectEntity.builder()
                .user(USER_ENTITY)
                .chat(CHAT_ENTITY)
                .numberThisMonth(NUMBER_THIS_MONTH)
                .numberPrevMonth(NUMBER_PREV_MONTH)
                .totalNumber(TOTAL_NUMBER)
                .build();
    }
}
