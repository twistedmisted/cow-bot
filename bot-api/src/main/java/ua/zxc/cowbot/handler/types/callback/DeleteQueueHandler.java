package ua.zxc.cowbot.handler.types.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.utils.ParseData;

import javax.persistence.EntityNotFoundException;

import static java.util.Objects.isNull;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.QUEUE_NOT_EXISTS;
import static ua.zxc.cowbot.utils.Constants.QUEUE_WITH_NAME_REMOVED;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

@Handler(value = "DELETE_QUEUE_BY_ID", commands = "delete_queue_by_id_")
@Slf4j
@RequiredArgsConstructor
public class DeleteQueueHandler implements HandlerStrategy {

    private final QueueService queueService;
    private final TelegramService telegramService;

    @Override
    public SendMessage handleMessage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try {
            return processDeletingQueue(callbackQuery);
        } catch (RuntimeException e) {
            log.warn("Cannot remove queue, something went wrong", e);
            return SendMessage.builder()
                    .chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "видалити чергу"))
                    .parseMode(HTML)
                    .build();
        }
    }

    private SendMessage processDeletingQueue(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Long queueId = ParseData.parseLongAfterLastUnderscore(callbackQuery.getData());
        String messageText;
        try {
            QueueDTO removedQueue = queueService.deleteQueue(queueId);
            if (isNull(removedQueue)) {
                throw new RuntimeException("Cannot remove queue with id: " + queueId);
            }
            messageText = String.format(QUEUE_WITH_NAME_REMOVED, removedQueue.getName());
        } catch (EntityNotFoundException e) {
            messageText = QUEUE_NOT_EXISTS;
        }
        telegramService.deleteMessage(callbackQuery.getMessage());
        return SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .parseMode(HTML)
                .build();
    }
}
