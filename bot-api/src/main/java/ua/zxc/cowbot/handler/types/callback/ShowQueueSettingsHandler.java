package ua.zxc.cowbot.handler.types.callback;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.utils.ParseData;

import static ua.zxc.cowbot.Keyboard.createKeyboardForQueueSettings;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.QUEUE_NOT_EXISTS;
import static ua.zxc.cowbot.utils.Constants.QUEUE_SETTINGS_TITLE;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

@Handler(value = "SHOW_QUEUE_SETTINGS", commands = "show_queue_settings_")
@Slf4j
public class ShowQueueSettingsHandler implements HandlerStrategy {

    private final QueueService queueService;

    private final TelegramService telegramService;

    public ShowQueueSettingsHandler(QueueService queueService, TelegramService telegramService) {
        this.queueService = queueService;
        this.telegramService = telegramService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try {
            showQueueSettings(callbackQuery);
        } catch (RuntimeException e) {
            log.warn("Cannot show queue settings, something went wrong", e);
            telegramService.answerCallbackQuery(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "відобразити налаштування для черги"))
                    .build()
            );
        }
        return new SendMessage();
    }

    private void showQueueSettings(CallbackQuery callbackQuery) {
        Long queueId = ParseData.parseLongAfterLastUnderscore(callbackQuery.getData());
        boolean exists = queueService.existsById(queueId);
        EditMessageText editMessageText = createEditMessageText(callbackQuery, exists);
        if (exists) {
            editMessageText.setReplyMarkup(createKeyboardForQueueSettings(queueId));
        }
        telegramService.editMessageText(editMessageText);
    }

    private EditMessageText createEditMessageText(CallbackQuery callbackQuery, boolean exists) {
        return EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(getMessageText(exists))
                .parseMode(HTML)
                .build();
    }

    private String getMessageText(boolean exists) {
        if (exists) {
            return QUEUE_SETTINGS_TITLE;
        }
        return QUEUE_NOT_EXISTS;
    }
}
