package ua.zxc.cowbot.handler.types.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.QueueTaskDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.QueueTaskService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.service.UserService;
import ua.zxc.cowbot.utils.ParseData;
import ua.zxc.cowbot.utils.TelegramUtils;

import static java.util.Objects.isNull;
import static ua.zxc.cowbot.utils.Constants.CHANGE_PLACE_NUMBER_INFO_MESSAGE;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;
import static ua.zxc.cowbot.utils.Constants.USER_NEED_REGISTRATION;

@Handler(value = "CHANGE_NUMBER_PLACES", commands = "change_number_places_")
@Slf4j
public class ChangeNumberPlacesHandler implements HandlerStrategy {

    private final QueueTaskService queueTaskService;
    private final QueueService queueService;
    private final TelegramService telegramService;
    private final UserService userService;

    public ChangeNumberPlacesHandler(@Qualifier("postgresqlQueueTaskService") QueueTaskService queueTaskService,
                                     QueueService queueService, TelegramService telegramService, UserService userService) {
        this.queueTaskService = queueTaskService;
        this.queueService = queueService;
        this.telegramService = telegramService;
        this.userService = userService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String messageText;
        try {
            Long queueId = ParseData.parseLongAfterLastUnderscore(callbackQuery.getData());
            if (queueExists(queueId)) {
                if (userExists(callbackQuery)) {
                    createQueueTaskToChangeNumberPlaces(callbackQuery.getFrom().getId(),
                            callbackQuery.getMessage().getChatId(), queueId);
                    messageText = CHANGE_PLACE_NUMBER_INFO_MESSAGE;
                } else {
                    messageText = TelegramUtils.mentionUser(callbackQuery.getFrom()) + USER_NEED_REGISTRATION;
                }
            } else {
                telegramService.deleteMessage(callbackQuery.getMessage());
                messageText = Constants.QUEUE_NOT_EXISTS;
            }
        } catch (RuntimeException e) {
            log.warn("Cannot create queue task to change number places of the queue", e);
            messageText = String.format(TRY_AGAIN_WHAT_SOMETHING, "створити запит на зміну кількості місць у черзі");
        }
        return SendMessage.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .text(messageText)
                .parseMode(HTML)
                .build();
    }

    private boolean userExists(CallbackQuery callbackQuery) {
        return userService.existsUserById(callbackQuery.getFrom().getId());
    }

    private boolean queueExists(Long queueId) {
        return queueService.existsById(queueId);
    }

    private void createQueueTaskToChangeNumberPlaces(Long userId, Long chatId, Long queueId) {
        QueueTaskDTO queueTaskToSave = QueueTaskDTO.builder()
                .userId(userId)
                .chatId(chatId)
                .name("CHANGE_NUMBER_PLACES_" + queueId)
                .numberTry(3)
                .build();
        queueTaskToSave = queueTaskService.insertQueueTask(queueTaskToSave);
        if (isNull(queueTaskToSave)) {
            throw new RuntimeException("Cannot create queue task to change number places of the queue with id: "
                    + queueId);
        }
    }
}
