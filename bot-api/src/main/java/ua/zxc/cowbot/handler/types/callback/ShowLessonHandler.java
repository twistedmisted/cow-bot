package ua.zxc.cowbot.handler.types.callback;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.LessonService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.utils.ParseData;

import static java.util.Objects.isNull;
import static ua.zxc.cowbot.Keyboard.createKeyboardForLesson;
import static ua.zxc.cowbot.utils.Constants.CANNOT_SHOW_LESSON_BY_ID;
import static ua.zxc.cowbot.utils.Constants.HTML;

@Handler(value = "SHOW_LESSON_BY_ID", commands = "show_lesson_by_id_")
@Slf4j
public class ShowLessonHandler implements HandlerStrategy {

    private final LessonService lessonService;

    private final TelegramService telegramService;

    public ShowLessonHandler(LessonService lessonService, TelegramService telegramService) {
        this.lessonService = lessonService;
        this.telegramService = telegramService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try {
            showLesson(callbackQuery);
        } catch (RuntimeException e) {
            log.warn("Cannot show lesson, maybe this is not exist", e);
            telegramService.answerCallbackQuery(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .text(CANNOT_SHOW_LESSON_BY_ID)
                    .build()
            );
        }
        return new SendMessage();
    }

    private void showLesson(CallbackQuery callbackQuery) {
        Long lessonId = ParseData.parseLongAfterLastUnderscore(callbackQuery.getData());
        LessonDTO lessonById = lessonService.getLessonById(lessonId);
        if (isNull(lessonById)) {
            throw new RuntimeException("Cannot get lesson by id: " + lessonId);
        }
        editMessageToShowLessonById(callbackQuery, lessonById);
    }

    private void editMessageToShowLessonById(CallbackQuery callbackQuery, LessonDTO lesson) {
        EditMessageText messageToEdit = EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(getLessonInformation(lesson))
                .parseMode(HTML)
                .replyMarkup(createKeyboardForLesson(lesson))
                .build();
        telegramService.editMessageText(messageToEdit);
    }

    private String getLessonInformation(LessonDTO lesson) {
        StringBuilder information = new StringBuilder();
        information.append("<b>Предмет:</b><i> ")
                .append(lesson.getName()).append("</i>")
                .append(System.lineSeparator())
                .append("<b>Викладач:</b><i> ")
                .append(lesson.getFullTeacherName()).append("</i>")
                .append(System.lineSeparator());
        if (lesson.getEmail() != null && !lesson.getEmail().isEmpty()) {
            information.append("<b>Пошта:</b><i> ")
                    .append(lesson.getEmail()).append("</i>")
                    .append(System.lineSeparator());
        }
        if (lesson.getPhone() != null && !lesson.getPhone().isEmpty()) {
            information.append("<b>Телефон:</b><i> ")
                    .append(lesson.getPhone()).append("</i>")
                    .append(System.lineSeparator());
        }
        return information.toString();
    }
}
