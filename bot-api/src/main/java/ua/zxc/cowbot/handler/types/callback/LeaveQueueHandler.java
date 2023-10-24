package ua.zxc.cowbot.handler.types.callback;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.PlaceService;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.utils.ParseData;

import static ua.zxc.cowbot.Keyboard.createKeyboardForQueue;
import static ua.zxc.cowbot.utils.Constants.*;
import static ua.zxc.cowbot.utils.TelegramUtils.mentionUser;

@Handler(value = "LEAVE_QUEUE_BY_ID", commands = "leave_queue_by_id_")
@Slf4j
public class LeaveQueueHandler implements HandlerStrategy {

    private final QueueService queueService;

    private final PlaceService placeService;

    private final TelegramService telegramService;

    public LeaveQueueHandler(QueueService queueService, PlaceService placeService, TelegramService telegramService) {
        this.queueService = queueService;
        this.placeService = placeService;
        this.telegramService = telegramService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try {
            return leavePlace(callbackQuery);
        } catch (RuntimeException e) {
            return SendMessage.builder()
                    .chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
                    .text(mentionUser(callbackQuery.getFrom()) + CANNOT_LEAVE_QUEUE)
                    .parseMode(HTML)
                    .build();
        }
    }

    private SendMessage leavePlace(CallbackQuery callbackQuery) {
        Long queueId = ParseData.parseLongAfterLastUnderscore(callbackQuery.getData());
        User user = callbackQuery.getMessage().getFrom();
        if (!placeExistsByUserIdAndQueueId(user.getId(), queueId)) {
            showNotificationThatUserIsNotInQueue(callbackQuery);
            return new SendMessage();
        }
        boolean placeRemoved = placeService.deletePlace(user.getId(), queueId);
        editKeyboardForQueueIfPlaceRemovedOrThrowRuntimeException(callbackQuery, placeRemoved, queueId);
        return SendMessage.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .text(mentionUser(user) + LEAVE_QUEUE)
                .parseMode(HTML)
                .build();
    }

    private void editKeyboardForQueueIfPlaceRemovedOrThrowRuntimeException(CallbackQuery callbackQuery,
                                                                           boolean placeRemoved,
                                                                           Long queueId) {
        if (!placeRemoved) {
            throw new RuntimeException("Cannot leave queue, cannot remove place");
        }
        QueueDTO queueById = queueService.getQueueById(queueId);
        if (queueById == null) {
            // TODO: maybe add new exception to send message that queue is removed
            throw new RuntimeException("Cannot leave queue, queue is null");
        }
        editKeyboardForQueue(callbackQuery, queueById);
    }

    private void editKeyboardForQueue(CallbackQuery callbackQuery, QueueDTO queue) {
        telegramService.editMessageReplyMarkup(EditMessageReplyMarkup.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .inlineMessageId(callbackQuery.getInlineMessageId())
                .replyMarkup(createKeyboardForQueue(queue))
                .build()
        );
    }

    private void showNotificationThatUserIsNotInQueue(CallbackQuery callbackQuery) {
        telegramService.answerCallbackQuery(AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text(USER_NOT_IN_QUEUE)
                .build()
        );
    }

    private boolean placeExistsByUserIdAndQueueId(Long userId, Long queueId) {
        return placeService.existsById(userId, queueId);
    }
}
