package ua.zxc.cowbot.helper;

import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.postgresql.entity.ChatEntity;
import ua.zxc.cowbot.postgresql.entity.QueueEntity;

import java.util.Collections;

public class QueueInitializr {

    private static final Long QUEUE_ID = 1L;
    private static final String QUEUE_NAME = "Queue Name";
    private static final Integer QUEUE_SIZE = 10;
    private static final ChatEntity CHAT_ENTITY = ChatInitializr.createEntity();

    public static QueueDTO createDTO() {
        return QueueDTO.builder()
                .id(QUEUE_ID)
                .name(QUEUE_NAME)
                .size(QUEUE_SIZE)
                .chatId(CHAT_ENTITY.getId())
                .build();
    }

    public static QueueDTO createDTOWithPlaces() {
        return QueueDTO.builder()
                .id(QUEUE_ID)
                .name(QUEUE_NAME)
                .size(QUEUE_SIZE)
                .chatId(CHAT_ENTITY.getId())
                .places(Collections.nCopies(11, PlaceInitializr.createDTO()))
                .build();
    }

    public static QueueEntity createEntity() {
        return QueueEntity.builder()
                .id(QUEUE_ID)
                .name(QUEUE_NAME)
                .size(QUEUE_SIZE)
                .chat(CHAT_ENTITY)
                .build();
    }
}
