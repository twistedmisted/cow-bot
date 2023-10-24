//package ua.zxc.cowbot.service.integration;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.jdbc.Sql;
//import ua.zxc.cowbot.dto.LessonDTO;
//import ua.zxc.cowbot.helper.LessonInitializr;
//import ua.zxc.cowbot.service.LessonService;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
//@Sql(
//        value = {
//                "classpath:/database/clear-tables.sql",
//                "classpath:/database/insert-chat.sql",
//                "classpath:/database/insert-lesson.sql"
//        },
//        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {"classpath:/database/clear-tables.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//public class LessonServiceImplIntTest {
//
//    @Autowired
//    private LessonService lessonService;
//
//    @Test
//    public void getLessonByIdWithExistingIdShouldReturnLesson() {
//        LessonDTO actualLessonDTO = lessonService.getLessonById(2L);
//
//        assertNotNull(actualLessonDTO);
//        assertEquals("second lesson", actualLessonDTO.getName());
//    }
//
//    @Test
//    public void getLessonByIdWithNotExistingIdShouldReturnLesson() {
//        LessonDTO actualLessonDTO = lessonService.getLessonById(10000L);
//
//        assertNull(actualLessonDTO);
//    }
//
//    @Test
//    public void getAllByChatIdWithExistingChatIdShouldReturnLessonList() {
//        List<LessonDTO> actualLessonsByChatId = lessonService.getAllByChatId(1L);
//
//        assertNotNull(actualLessonsByChatId);
//        assertFalse(actualLessonsByChatId.isEmpty());
//        assertEquals(2, actualLessonsByChatId.size());
//    }
//
//    @Test
//    public void getAllByChatIdWithNotExistingChatIdShouldReturnEmptyList() {
//        List<LessonDTO> actualLessonsByChatId = lessonService.getAllByChatId(100L);
//
//        assertNotNull(actualLessonsByChatId);
//        assertTrue(actualLessonsByChatId.isEmpty());
//    }
//
//    @Test
//    public void insertLessonWithNotExistingLessonShouldReturnNewLesson() {
//        LessonDTO lessonToSave = LessonInitializr.createDTOWithoutId();
//        LessonDTO actualLessonDTO = lessonService.insertLesson(lessonToSave);
//
//        assertNotNull(actualLessonDTO);
//        assertNotNull(actualLessonDTO.getId());
//        assertEquals(lessonToSave.getFullTeacherName(), actualLessonDTO.getFullTeacherName());
//    }
//
//    @Test
//    public void insertLessonWithExistingLessonShouldReturnNull() {
//        LessonDTO lessonToSave = LessonInitializr.createDTOWithNameAndChatId("first lesson", 1);
//        LessonDTO actualLessonDTO = lessonService.insertLesson(lessonToSave);
//
//        assertNull(actualLessonDTO);
//    }
//
//    @Test
//    public void deleteLessonWithExistingIdShouldReturnTrue() {
//        LessonDTO isLessonRemoved = lessonService.deleteLesson(3L);
//
//        assertNotNull(isLessonRemoved);
//    }
//
//    @Test
//    public void deleteLessonWithNotExistingIdShouldReturnFalse() {
//        LessonDTO removedLesson = lessonService.deleteLesson(333L);
//
//        assertNull(removedLesson);
//    }
//}
