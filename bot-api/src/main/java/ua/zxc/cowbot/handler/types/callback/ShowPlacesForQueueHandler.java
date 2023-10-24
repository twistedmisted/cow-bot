package ua.zxc.cowbot.handler.types.callback;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;

import static java.util.Objects.isNull;
import static ua.zxc.cowbot.Keyboard.createKeyboardForQueue;
import static ua.zxc.cowbot.utils.Constants.*;

@Handler(value = "SHOW_PLACES_FOR_QUEUE_BY_ID", commands = "show_places_for_queue_by_id_")
@Slf4j
public class ShowPlacesForQueueHandler implements HandlerStrategy {

    private final QueueService queueService;

    private final TelegramService telegramService;

    public ShowPlacesForQueueHandler(QueueService queueService, TelegramService telegramService) {
        this.queueService = queueService;
        this.telegramService = telegramService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try {
            showQueue(callbackQuery);
        } catch (RuntimeException e) {
            log.warn("Cannot show queue by id, something went wrong", e);
            telegramService.answerCallbackQuery(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "не вдалось вивести чергу"))
                    .build()
            );
        }
        return new SendMessage();
    }

    private void showQueue(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long queueId = Long.parseLong(data.substring(data.lastIndexOf('_') + 1));
        QueueDTO queue = queueService.getQueueById(queueId);
        EditMessageText editMessageText = createEditMessageText(queue, callbackQuery);
        if (!isNull(queue)) {
            editMessageText.setReplyMarkup(createKeyboardForQueue(queue));
        }
        telegramService.editMessageText(editMessageText);
    }

    private EditMessageText createEditMessageText(QueueDTO queue, CallbackQuery callbackQuery) {
        return EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(createMessageText(queue))
                .parseMode(HTML)
                .build();
    }

    private String createMessageText(QueueDTO queue) {
        if (isNull(queue)) {
            return QUEUE_REMOVED;
        }
        return "<b>" + queue.getName() + "</b>";
    }
}
