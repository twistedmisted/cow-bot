package ua.zxc.cowbot.handler.types.other;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.dto.QueueTaskDTO;
import ua.zxc.cowbot.exception.IllegalSizeRangeException;
import ua.zxc.cowbot.exception.SizeValueOutOfBoundsException;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.QueueTaskService;
import ua.zxc.cowbot.utils.ParseData;

import javax.persistence.EntityNotFoundException;

import static java.lang.Integer.parseInt;
import static java.util.Objects.isNull;
import static ua.zxc.cowbot.utils.Constants.ATTEMPTS_OVER;
import static ua.zxc.cowbot.utils.Constants.ENTER_NUMBER_PLACES;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.QUEUE_REMOVED_CANNOT_CHANGE_NUMBER_PLACES;
import static ua.zxc.cowbot.utils.Constants.QUEUE_SIZE_IS_BIGGER_THAN_MAX_PLACE_NUMBER;
import static ua.zxc.cowbot.utils.Constants.QUEUE_SIZE_RANGE;
import static ua.zxc.cowbot.utils.Constants.THIS_NOT_NUMBER;
import static ua.zxc.cowbot.utils.TelegramUtils.isCallbackQueryUpdate;

@Handler(value = "QUEUE_TASK")
@Slf4j
public class QueueTaskHandler implements HandlerStrategy {

    private final QueueTaskService queueTaskService;

    private final QueueService queueService;

    public QueueTaskHandler(QueueTaskService queueTaskService, QueueService queueService) {
        this.queueTaskService = queueTaskService;
        this.queueService = queueService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        String messageText;
        try {
            return getSendMessageForUserWithTask(update);
        } catch (NumberFormatException e) {
            log.warn("Cannot parse message text to number", e);
            messageText = THIS_NOT_NUMBER;
        } catch (IllegalSizeRangeException e) {
            log.warn("The new size must be between 5 and 30", e);
            messageText = QUEUE_SIZE_RANGE;
        } catch (SizeValueOutOfBoundsException e) {
            log.warn("The new size outs of bounds", e);
            messageText = QUEUE_SIZE_IS_BIGGER_THAN_MAX_PLACE_NUMBER;
        } catch (RuntimeException e) {
            log.warn("Cannot process change number places, something went wrong", e);
            messageText = QUEUE_REMOVED_CANNOT_CHANGE_NUMBER_PLACES;
        }
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(messageText)
                .parseMode(HTML)
                .build();
    }

    private SendMessage getSendMessageForUserWithTask(Update update) {
        Long chatId;
        String messageText;
        if (isCallbackQueryUpdate(update)) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageText = ENTER_NUMBER_PLACES;
        } else {
            chatId = update.getMessage().getChatId();
            messageText = changeCountPlacesAndGetMessageText(update.getMessage());
        }
        return createMessage(chatId, messageText);
    }

    private String changeCountPlacesAndGetMessageText(Message message) {
        QueueTaskDTO queueTask = getQueueTaskOrThrowRuntimeException(message);
        if (queueTask.isAttemptsOver()) {
            queueTaskService.deleteQueueTask(queueTask.getId());
            return ATTEMPTS_OVER;
        }
        Integer size = parseIntFromMessageText(message.getText());
        Long queueId = ParseData.parseLongAfterLastUnderscore(queueTask.getName());
        QueueDTO updatedQueue = updateQueueSize(queueId, size);
        queueTaskService.deleteQueueTask(queueTask.getId());
        if (isNull(updatedQueue)) {
            return QUEUE_REMOVED_CANNOT_CHANGE_NUMBER_PLACES;
        }
        return "<b>Кількість місць для черги '" + updatedQueue.getName() + "' змінено успіншо.</b>";
    }

    private QueueTaskDTO getQueueTaskOrThrowRuntimeException(Message message) {
        QueueTaskDTO queueTask = queueTaskService.getQueueTaskByUserIdAndChatId(message.getFrom().getId(),
                message.getChatId());
        throwRuntimeExceptionIfQueueTaskIsNull(queueTask);
        return queueTask;
    }

    private QueueDTO updateQueueSize(Long queueId, Integer size) {
        QueueDTO updatedQueue = null;
        try {
            updatedQueue = queueService.updateQueueSizeByQueueId(queueId, size);
        } catch (EntityNotFoundException e) {
            log.warn("Cannot get place for queue, probably queue is removed", e);
        }
        return updatedQueue;
    }

    private Integer parseIntFromMessageText(String text) {
        return parseInt(text);
    }

    private static void throwRuntimeExceptionIfQueueTaskIsNull(QueueTaskDTO queueTask) {
        if (isNull(queueTask)) {
            throw new RuntimeException("Cannot get queue task to change number places of the queue, queue task is null");
        }
    }

    private static SendMessage createMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(HTML)
                .build();
    }
}
