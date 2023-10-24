package ua.zxc.cowbot.helper;

import ua.zxc.cowbot.dto.PlaceDTO;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.postgresql.entity.PlaceEntity;
import ua.zxc.cowbot.postgresql.entity.QueueEntity;
import ua.zxc.cowbot.postgresql.entity.UserEntity;
import ua.zxc.cowbot.postgresql.entity.embeddedid.UserQueueId;

public class PlaceInitializr {

    private static final UserDTO USER_DTO = UserInitializr.createUserDTO();
    private static final UserEntity USER_ENTITY = UserInitializr.createUserEntity();
    private static final QueueDTO QUEUE_DTO = QueueInitializr.createDTO();
    private static final QueueEntity QUEUE_ENTITY = QueueInitializr.createEntity();
    private static final Integer NUMBER = 20;

    public static PlaceDTO createDTO() {
        return PlaceDTO.builder()
                .userId(USER_DTO.getId())
                .queueId(QUEUE_DTO.getId())
                .user(USER_DTO)
                .queue(QUEUE_DTO)
                .number(NUMBER)
                .build();
    }

    public static PlaceDTO createDTO(Long userId, Integer placeNumber) {
        UserDTO userDTO = UserInitializr.createUserDTOWithCustomId(userId);
        return PlaceDTO.builder()
                .userId(userId)
                .queueId(QUEUE_DTO.getId())
                .user(userDTO)
                .queue(QUEUE_DTO)
                .number(placeNumber)
                .build();
    }

    public static PlaceEntity createEntity() {
        return PlaceEntity.builder()
                .id(UserQueueId.builder().userId(USER_ENTITY.getId()).queueId(QUEUE_ENTITY.getId()).build())
                .user(USER_ENTITY)
                .queue(QUEUE_ENTITY)
                .number(NUMBER)
                .build();
    }
}
