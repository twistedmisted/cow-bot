package ua.zxc.cowbot.handler.types.callback;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.utils.ParseData;

import static ua.zxc.cowbot.Keyboard.createKeyboardForQueues;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_PAGE_SIZE;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.QUEUE_LIST_TITLE;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

@Handler(value = "SHOW_QUEUE_PAGE", commands = "show_queue_page_")
@Slf4j
public class ShowQueuePageHandler implements HandlerStrategy {

    private final QueueService queueService;

    private final TelegramService telegramService;

    public ShowQueuePageHandler(QueueService queueService, TelegramService telegramService) {
        this.queueService = queueService;
        this.telegramService = telegramService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try {
            printQueues(callbackQuery);
        } catch (RuntimeException e) {
            log.warn("Cannot show queue page, something went wrong", e);
            telegramService.answerCallbackQuery(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "відобразити сторінку з чергами"))
                    .build()
            );
        }
        return new SendMessage();
    }

    private void printQueues(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        int pageNumber = ParseData.parseIntAfterLastUnderscore(callbackQuery.getData());
        PageBO<QueueDTO> queuePage = queueService.getAllByChatId(chatId, pageNumber, DEFAULT_PAGE_SIZE);
        if (queuePage.isEmpty()) {
            throw new RuntimeException("Cannot open queue page: [pageNumber = '" + pageNumber + "']");
        }
        editMessageToPrintQueues(callbackQuery, queuePage);
    }

    private void editMessageToPrintQueues(CallbackQuery callbackQuery, PageBO<QueueDTO> queuePage) {
        telegramService.editMessageText(EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(QUEUE_LIST_TITLE)
                .parseMode(HTML)
                .replyMarkup(createKeyboardForQueues(queuePage))
                .build()
        );
    }
}
