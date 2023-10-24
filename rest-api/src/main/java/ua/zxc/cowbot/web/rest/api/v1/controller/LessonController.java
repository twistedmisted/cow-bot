package ua.zxc.cowbot.web.rest.api.v1.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.notification.LessonNotification;
import ua.zxc.cowbot.service.LessonService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Hidden
@Tag(name = "Lesson", description = "The Lesson API")
@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    private final LessonNotification lessonNotification;

    public LessonController(LessonService lessonService, LessonNotification lessonNotification) {
        this.lessonService = lessonService;
        this.lessonNotification = lessonNotification;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getLesson(@PathVariable Long id) {
        Map<String, Object> responseBody = new HashMap<>();
        LessonDTO lesson = lessonService.getLessonById(id);
        if (isNull(lesson)) {
            responseBody.put("message", "Не вдається знайти предмет. Схоже його було видалено, якщо ні, то спробуйте ще раз.");
        } else {
            responseBody.put("lesson", lesson);
        }
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping("/chat/{id}")
    public ResponseEntity<Map<String, Object>> getLessonsByChatId(@PathVariable Long id) {
        Map<String, Object> responseBody = new HashMap<>();
        List<LessonDTO> lessons = lessonService.getAllByChatId(id);
        if (lessons.isEmpty()) {
            responseBody.put("message", "Схоже в цьому чаті нема предметів.");
        } else {
            responseBody = lessons.stream()
                    .collect(Collectors.toMap(lesson -> String.valueOf(lesson.getId()), LessonDTO::getName));
        }
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addLesson(@Valid @RequestBody LessonDTO lesson) {
        Map<String, Object> responseBody = new HashMap<>();
        LessonDTO savedLesson = lessonService.insertLesson(lesson);
        if (isNull(savedLesson)) {
            responseBody.put("message", "Не вдалось зберегти предмет. Перевірте правильність введених даних та спробуйте ще раз.");
        } else {
            responseBody.put("message", "Предмет '" + savedLesson.getName() + "' успішно збережено.");
            lessonNotification.sendAddedNotification(savedLesson);
        }
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateLesson(@RequestBody LessonDTO lesson) {
        Map<String, Object> responseBody = new HashMap<>();
        LessonDTO updatedLesson = lessonService.updateLesson(lesson);
        if (isNull(updatedLesson)) {
            responseBody.put("message", "Не вдалось оновити предмет. Перевірте правильність введених даних та спробуйте ще раз.");
        } else {
            responseBody.put("lesson", "Предмет '" + updatedLesson.getName() + "' успішно оновлено.");
            lessonNotification.sendUpdatedNotification(updatedLesson);
        }
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

}
