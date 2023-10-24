package ua.zxc.cowbot.helper;

import ua.zxc.cowbot.dto.QueueTaskDTO;
import ua.zxc.cowbot.postgresql.entity.QueueTaskEntity;

public class QueueTaskInitializr {

    private static final Long ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long CHAT_ID = 1L;
    private static final String NAME = "Queue task name";
    private static final Integer NUMBER_TRY = 3;

    public static QueueTaskDTO createDTO() {
        return QueueTaskDTO.builder()
                .id(ID)
                .userId(USER_ID)
                .chatId(CHAT_ID)
                .name(NAME)
                .numberTry(NUMBER_TRY)
                .build();
    }

    public static QueueTaskEntity createEntity() {
        return QueueTaskEntity.builder()
                .id(ID)
                .userId(USER_ID)
                .chatId(CHAT_ID)
                .name(NAME)
                .numberTry(NUMBER_TRY)
                .build();
    }
}
