package ua.zxc.cowbot.handler.types.callback;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.LessonService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.utils.ParseData;

import static ua.zxc.cowbot.Keyboard.createKeyboardForLessonSettings;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.LESSON_NOT_EXISTS;
import static ua.zxc.cowbot.utils.Constants.LESSON_SETTINGS_TITLE;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

@Handler(value = "SHOW_LESSON_SETTINGS", commands = "show_lesson_settings_by_id_")
@Slf4j
public class ShowLessonSettingsHandler implements HandlerStrategy {

    private final LessonService lessonService;

    private final TelegramService telegramService;

    public ShowLessonSettingsHandler(LessonService lessonService, TelegramService telegramService) {
        this.lessonService = lessonService;
        this.telegramService = telegramService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try {
            showLessonSettings(callbackQuery);
        } catch (RuntimeException e) {
            log.warn("Cannot show lesson settings, something went wrong", e);
            telegramService.answerCallbackQuery(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "відобразити налаштування для предмета"))
                    .build()
            );
        }
        return new SendMessage();
    }

    private void showLessonSettings(CallbackQuery callbackQuery) {
        Long lessonId = ParseData.parseLongAfterLastUnderscore(callbackQuery.getData());
        boolean exists = lessonService.existsById(lessonId);
        EditMessageText editMessageText = createEditMessageText(callbackQuery, exists);
        if (exists) {
            editMessageText.setReplyMarkup(createKeyboardForLessonSettings(lessonId));
        }
        telegramService.editMessageText(editMessageText);
    }

    private EditMessageText createEditMessageText(CallbackQuery callbackQuery, boolean exists) {
        return EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .text(getMessageText(exists))
                .messageId(callbackQuery.getMessage().getMessageId())
                .parseMode(HTML)
                .build();
    }

    private String getMessageText(boolean exists) {
        if (exists) {
            return LESSON_SETTINGS_TITLE;
        }
        return LESSON_NOT_EXISTS;
    }
}
