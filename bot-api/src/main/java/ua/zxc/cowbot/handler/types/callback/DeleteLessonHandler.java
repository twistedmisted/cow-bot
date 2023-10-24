package ua.zxc.cowbot.handler.types.callback;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.LessonService;
import ua.zxc.cowbot.utils.ParseData;

import static java.util.Objects.isNull;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.LESSON_WITH_NAME_REMOVED;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

@Handler(value = "DELETE_LESSON_BY_ID", commands = "delete_lesson_by_id_")
@Slf4j
public class DeleteLessonHandler implements HandlerStrategy {

    private final LessonService lessonService;

    public DeleteLessonHandler(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try {
            return processDeletingLesson(callbackQuery);
        } catch (RuntimeException e) {
            log.warn("Cannot remove lesson, something went wrong", e);
            return SendMessage.builder()
                    .chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "видалити предмет"))
                    .parseMode(HTML)
                    .build();
        }
    }

    private SendMessage processDeletingLesson(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Long lessonId = ParseData.parseLongAfterLastUnderscore(callbackQuery.getData());
        LessonDTO removedLesson = lessonService.deleteLesson(lessonId);
        if (isNull(removedLesson)) {
            throw new RuntimeException("Cannot remove lesson with id: " + lessonId);
        }
        return SendMessage.builder()
                .chatId(chatId)
                .text(String.format(LESSON_WITH_NAME_REMOVED, removedLesson.getName()))
                .parseMode(HTML)
                .build();
    }
}
