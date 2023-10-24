package ua.zxc.cowbot.helper;

import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.postgresql.entity.ChatEntity;
import ua.zxc.cowbot.postgresql.entity.LessonEntity;

public class LessonInitializr {

    private static final long ID = 1L;
    private static final String NAME = "name";
    private static final String FULL_TEACHER_NAME = "Full Teacher Name";
    private static final String URL = "url.com";
    private static final String EMAIL = "email@gmail.com";
    private static final String PHONE = "+380999999999";
    private static final ChatEntity CHAT_ENTITY = ChatInitializr.createEntity();

    public static LessonEntity createEntity() {
        return LessonEntity.builder()
                .id(ID)
                .name(NAME)
                .fullTeacherName(FULL_TEACHER_NAME)
                .url(URL)
                .email(EMAIL)
                .phone(PHONE)
                .chat(CHAT_ENTITY)
                .build();
    }

    public static LessonDTO createDTO() {
        return LessonDTO.builder()
                .id(ID)
                .name(NAME)
                .fullTeacherName(FULL_TEACHER_NAME)
                .url(URL)
                .email(EMAIL)
                .phone(PHONE)
                .chatId(CHAT_ENTITY.getId())
                .build();
    }

    public static LessonDTO createDTOWithoutId() {
        return LessonDTO.builder()
                .name(NAME)
                .fullTeacherName(FULL_TEACHER_NAME)
                .url(URL)
                .email(EMAIL)
                .phone(PHONE)
                .chatId(CHAT_ENTITY.getId())
                .build();
    }

    public static LessonDTO createDTOWithNameAndChatId(String name, long chatId) {
        return LessonDTO.builder()
                .id(ID)
                .name(name)
                .fullTeacherName(FULL_TEACHER_NAME)
                .url(URL)
                .email(EMAIL)
                .phone(PHONE)
                .chatId(chatId)
                .build();
    }
}
