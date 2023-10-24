package ua.zxc.cowbot.handler.types.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;

import static ua.zxc.cowbot.Keyboard.createKeyboardForQueues;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_NUMBER_PAGE;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_PAGE_SIZE;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.NOTHING_TO_SHOW;
import static ua.zxc.cowbot.utils.Constants.QUEUE_LIST_TITLE;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

@Handler(value = "QUEUES", commands = "/queues")
@Slf4j
@RequiredArgsConstructor
public class QueuesHandler implements HandlerStrategy {

    private final QueueService queueService;
    private final TelegramService telegramService;

    @Override
    public SendMessage handleMessage(Update update) {
        telegramService.deleteMessage(update.getMessage());
        try {
            return printQueues(update);
        } catch (RuntimeException e) {
            Long chatId = update.getMessage().getChatId();
            log.warn("Cannot print queues in chat with id: {}", chatId, e);
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "вивести список черг"))
                    .parseMode(HTML)
                    .build();
        }
    }

    public SendMessage printQueues(Update update) {
        Long chatId = update.getMessage().getChatId();
        PageBO<QueueDTO> queuePage = queueService.getAllByChatId(chatId, DEFAULT_NUMBER_PAGE, DEFAULT_PAGE_SIZE);
        return createMessageForQueues(queuePage, chatId);
    }

    private SendMessage createMessageForQueues(PageBO<QueueDTO> queuePage, Long chatId) {
        boolean pageEmpty = queuePage.isEmpty();
        SendMessage messageToSend = SendMessage.builder()
                .chatId(chatId)
                .text(getMessageText(pageEmpty))
                .parseMode(HTML)
                .build();
        if (!pageEmpty) {
            messageToSend.setReplyMarkup(createKeyboardForQueues(queuePage));
        }
        return messageToSend;
    }

    private String getMessageText(boolean empty) {
        if (empty) {
            return NOTHING_TO_SHOW;
        }
        return QUEUE_LIST_TITLE;
    }
}
