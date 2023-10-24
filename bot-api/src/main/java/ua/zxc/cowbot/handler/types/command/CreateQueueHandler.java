package ua.zxc.cowbot.handler.types.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.exception.ObjectAlreadyExistsException;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_QUEUE_SIZE;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.CHAT_NEED_REGISTRATION;
import static ua.zxc.cowbot.utils.Constants.QUEUE_ALREADY_EXISTS_WITH_NAME;
import static ua.zxc.cowbot.utils.Constants.QUEUE_CREATED;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

@Handler(value = "CREATE_QUEUE", commands = "/create")
@Slf4j
@RequiredArgsConstructor
public class CreateQueueHandler implements HandlerStrategy {

    private final QueueService queueService;
    private final TelegramService telegramService;

    @Override
    public SendMessage handleMessage(Update update) {
        telegramService.deleteMessage(update.getMessage());
        String messageText;
        try {
            messageText = createQueueAndReturnMessageText(update.getMessage());
        } catch (NoSuchElementException e) {
            log.warn("Cannot create queue for chat because this is not registered: [chatId = '{}']",
                    update.getMessage().getChat(), e);
            messageText = CHAT_NEED_REGISTRATION;
        } catch (RuntimeException e) {
            log.warn("Cannot create queue for chat with id: {}", update.getMessage().getChatId(), e);
            messageText = format(TRY_AGAIN_WHAT_SOMETHING, "створити чергу");
        }
        return SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text(messageText)
                .parseMode(HTML)
                .build();
    }

    private String createQueueAndReturnMessageText(Message message) {
        QueueDTO queueToSave = createQueueToSave(message);
        try {
            queueToSave = queueService.insertQueue(queueToSave);
            if (queueToSave == null) {
                throw new RuntimeException("Cannot create queue, queueToSave is null");
            }
            return format(QUEUE_CREATED, queueToSave.getName());
        } catch (ObjectAlreadyExistsException e) {
            log.warn("Cannot create queue: {}", queueToSave, e);
            return format(QUEUE_ALREADY_EXISTS_WITH_NAME, queueToSave.getName());
        }
    }

    private QueueDTO createQueueToSave(Message message) {
        return QueueDTO.builder()
                .name(parseQueueName(message.getText()))
                .size(DEFAULT_QUEUE_SIZE)
                .chatId(message.getChatId())
                .build();
    }

    private String parseQueueName(String messageText) {
        int startWith = messageText.indexOf(' ') + 1;
        if (startWith == 0) {
            return format("Черга (%s)", LocalDate.now().format(ISO_DATE));
        }
        return messageText.substring(startWith);
    }
}
