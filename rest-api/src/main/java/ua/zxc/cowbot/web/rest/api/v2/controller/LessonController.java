package ua.zxc.cowbot.web.rest.api.v2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.notification.LessonNotification;
import ua.zxc.cowbot.service.LessonService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Lesson", description = "The Lesson API")
@RestController("LessonController2")
@RequestMapping("/api/v2/lessons")
public class LessonController {

    private final LessonService lessonService;

    private final LessonNotification lessonNotification;

    public LessonController(LessonService lessonService, LessonNotification lessonNotification) {
        this.lessonService = lessonService;
        this.lessonNotification = lessonNotification;
    }

    @Operation(
            summary = "Get the lesson by id",
            description = "Get the lesson by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(schemaProperties = @SchemaProperty(
                                    name = "lesson",
                                    schema = @Schema(ref = "#/components/schemas/Lesson")
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    ),
                    @ApiResponse(
                            responseCode = "405",
                            description = "Method Not Allowed",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    )
            }
    )
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getLesson(@Parameter(description = "Lesson id") @PathVariable Long id) {
        Map<String, Object> responseBody = new HashMap<>();
        LessonDTO lesson = lessonService.getLessonById(id);
        if (isNull(lesson)) {
            responseBody.put("message", "Не вдається знайти предмет. " +
                    "Схоже його було видалено, якщо ні, то спробуйте ще раз.");
        } else {
            responseBody.put("lesson", lesson);
        }
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @Operation(
            summary = "Get chat's lessons",
            description = "Get the list of lessons by chat id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(schemaProperties = @SchemaProperty(
                                    name = "lesson",
                                    array = @ArraySchema(schema = @Schema(implementation = LessonDTO.class))
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    ),
                    @ApiResponse(
                            responseCode = "405",
                            description = "Method Not Allowed",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    )
            }
    )
    @GetMapping(value = "/chat/{chatId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getLessonsByChatId(@Parameter(description = "Chat id")
                                                                  @PathVariable Long chatId) {
        Map<String, Object> responseBody = new HashMap<>();
        List<LessonDTO> lessons = lessonService.getAllByChatId(chatId);
        if (lessons.isEmpty()) {
            responseBody.put("message", "Схоже в цьому чаті нема предметів.");
        } else {
            responseBody = lessons.stream()
                    .collect(Collectors.toMap(lesson -> String.valueOf(lesson.getId()), LessonDTO::getName));
        }
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @Operation(
            summary = "Create new lesson",
            description = "Create new lesson for chat",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Success"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    ),
                    @ApiResponse(
                            responseCode = "405",
                            description = "Method Not Allowed",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    )
            }
    )
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> addLesson(@Valid @RequestBody LessonDTO lesson) {
        Map<String, Object> responseBody = new HashMap<>();
        LessonDTO savedLesson = lessonService.insertLesson(lesson);
        if (isNull(savedLesson)) {
            responseBody.put("message", "Не вдалось зберегти предмет. " +
                    "Перевірте правильність введених даних та спробуйте ще раз.");
        } else {
            responseBody.put("message", "Предмет '" + savedLesson.getName() + "' успішно збережено.");
            lessonNotification.sendAddedNotification(savedLesson);
        }
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @Operation(
            summary = "Update the lesson",
            description = "Update the existing lesson by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Success"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    ),
                    @ApiResponse(
                            responseCode = "405",
                            description = "Method Not Allowed",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(ref = "#/components/schemas/Error"))
                    )
            }
    )
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateLesson(@Parameter(description = "Lesson id") @PathVariable Long id,
                                                            @RequestBody LessonDTO lesson) {
        Map<String, Object> responseBody = new HashMap<>();
        lesson.setId(id);
        LessonDTO updatedLesson = lessonService.updateLesson(lesson);
        if (isNull(updatedLesson)) {
            responseBody.put("message", "Не вдалось оновити предмет. " +
                    "Перевірте правильність введених даних та спробуйте ще раз.");
        } else {
            responseBody.put("lesson", "Предмет '" + updatedLesson.getName() + "' успішно оновлено.");
            lessonNotification.sendUpdatedNotification(updatedLesson);
        }
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
