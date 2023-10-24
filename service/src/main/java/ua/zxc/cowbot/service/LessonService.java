package ua.zxc.cowbot.service;

import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.LessonDTO;

import java.util.List;

public interface LessonService {

    LessonDTO getLessonById(Long lessonId);

    List<LessonDTO> getAllByChatId(Long chatId);

    PageBO<LessonDTO> getAllByChatId(Long chatId, Integer pageNum, Integer pageSize);

    LessonDTO insertLesson(LessonDTO lessonDTO);

    LessonDTO updateLesson(LessonDTO lessonDTO);

    LessonDTO deleteLesson(Long lessonId);

    boolean existsById(Long lessonId);
}
