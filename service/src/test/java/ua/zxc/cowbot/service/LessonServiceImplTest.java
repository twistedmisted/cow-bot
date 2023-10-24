package ua.zxc.cowbot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.zxc.cowbot.ServiceSpringBootTest;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.helper.ChatInitializr;
import ua.zxc.cowbot.helper.LessonInitializr;
import ua.zxc.cowbot.postgresql.entity.ChatEntity;
import ua.zxc.cowbot.postgresql.entity.LessonEntity;
import ua.zxc.cowbot.postgresql.repository.ChatRepository;
import ua.zxc.cowbot.postgresql.repository.LessonRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LessonServiceImplTest extends ServiceSpringBootTest {

    private static final LessonEntity LESSON_ENTITY = LessonInitializr.createEntity();
    private static final LessonDTO LESSON_DTO = LessonInitializr.createDTO();
    private static final ChatEntity CHAT_ENTITY = ChatInitializr.createEntity();

    @Autowired
    private LessonService lessonService;

    @MockBean
    private LessonRepository lessonRepository;

    @MockBean
    private ChatRepository chatRepository;

    @Test
    public void getLessonByIdWithExistingIdShouldReturnLesson() {
        when(lessonRepository.findById(anyLong())).thenReturn(Optional.of(LESSON_ENTITY));

        LessonDTO actualLessonDTO = lessonService.getLessonById(1L);

        assertNotNull(actualLessonDTO);
        assertDtoEqualsEntity(actualLessonDTO, LESSON_ENTITY);
    }

    @Test
    public void getLessonByIdWithNotExistingIdShouldReturnNull() {
        LessonDTO actualLessonDTO = lessonService.getLessonById(1L);

        assertNull(actualLessonDTO);
    }

    @Test
    public void getAllByChatIdWithExistingIdShouldReturnLessonList() {
        when(lessonRepository.findAllByChatIdOrderByNameAsc(anyLong())).thenReturn(Collections.singletonList(LESSON_ENTITY));

        List<LessonDTO> actualLessonList = lessonService.getAllByChatId(1L);

        assertNotNull(actualLessonList);
        assertFalse(actualLessonList.isEmpty());
        assertDtoEqualsEntity(actualLessonList.get(0), LESSON_ENTITY);
    }

    @Test
    public void getAllByChatIdWithNotExistingIdShouldReturnEmptyList() {
        List<LessonDTO> actualLessonList = lessonService.getAllByChatId(1L);

        assertNotNull(actualLessonList);
        assertTrue(actualLessonList.isEmpty());
    }

    @Test
    public void insertLessonWitNotExistingLessonShouldReturnNewLesson() {
        when(lessonRepository.existsByNameAndChatId(anyString(), anyLong())).thenReturn(false);
        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(CHAT_ENTITY));
        when(lessonRepository.save(any(LessonEntity.class))).thenReturn(LESSON_ENTITY);

        LessonDTO actualLessonDTO = lessonService.insertLesson(LESSON_DTO);

        assertNotNull(actualLessonDTO);
        assertDtoEqualsEntity(actualLessonDTO, LESSON_ENTITY);

        verify(lessonRepository, times(1)).existsByNameAndChatId(anyString(), anyLong());
        verify(chatRepository, times(1)).findById(anyLong());
        verify(lessonRepository, times(1)).save(any(LessonEntity.class));
    }

    @Test
    public void insertLessonWithExistingLessonShouldReturnNull() {
        when(lessonRepository.existsByNameAndChatId(anyString(), anyLong())).thenReturn(true);

        LessonDTO actualLessonDTO = lessonService.insertLesson(LESSON_DTO);

        assertNull(actualLessonDTO);

        verify(lessonRepository, times(1)).existsByNameAndChatId(anyString(), anyLong());
        verify(chatRepository, times(0)).findById(anyLong());
        verify(lessonRepository, times(0)).save(any(LessonEntity.class));
    }

    @Test
    public void deleteLessonWithExistingIdShouldReturnTrue() {
        when(lessonRepository.deleteLessonById(anyLong()))
                .thenReturn(Collections.singletonList(LessonInitializr.createEntity()));

        LessonDTO removedLesson = lessonService.deleteLesson(1L);

        assertNotNull(removedLesson);
    }

    @Test
    public void deleteLessonWithNotExistingIdShouldReturnFalse() {
        doThrow(new RuntimeException()).when(lessonRepository).deleteLessonById(anyLong());

        LessonDTO removedLesson = lessonService.deleteLesson(1L);

        assertNull(removedLesson);
    }

    private void assertDtoEqualsEntity(LessonDTO dto, LessonEntity entity) {
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getUrl(), entity.getUrl());
        assertEquals(dto.getFullTeacherName(), entity.getFullTeacherName());
        assertEquals(dto.getEmail(), entity.getEmail());
        assertEquals(dto.getPhone(), entity.getPhone());
        assertEquals(dto.getChatId(), entity.getChat().getId());
    }
}
