package ua.zxc.cowbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.zxc.cowbot.CowBot;
import ua.zxc.cowbot.exception.CallbackQueryException;
import ua.zxc.cowbot.exception.MessageException;
import ua.zxc.cowbot.service.TelegramService;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Service
@Slf4j
public class TelegramServiceImpl implements TelegramService {

    private final CowBot cowBot;

    public TelegramServiceImpl(CowBot cowBot) {
        this.cowBot = cowBot;
    }

    @PostConstruct
    private void postConstruct() throws Exception {
        try {
            cowBot.execute(SetWebhook.builder()
                    .url(cowBot.getBotPath())
                    .allowedUpdates(Collections.emptyList())
                    .dropPendingUpdates(true)
                    .build());
        } catch (TelegramApiException e) {
            log.warn("Cannot set webhook path", e);
            throw new Exception("Cannot set webhook path", e);
        }
    }

    @Override
    public void sendMessage(SendMessage sendMessage) {
        try {
            cowBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn("Cannot send message: {}", sendMessage, e);
            throw new MessageException("Cannot send message: " + sendMessage, e);
        }
    }

    @Override
    public void editMessageText(EditMessageText editMessageText) {
        try {
            cowBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            log.warn("Cannot edit message: {}", editMessageText, e);
            throw new MessageException("Cannot edit message: " + editMessageText, e);
        }
    }

    @Override
    public void editMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup) {
        try {
            cowBot.execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            log.warn("Cannot edit message reply markup: {}", editMessageReplyMarkup, e);
            throw new MessageException("Cannot edit message reply markup: " + editMessageReplyMarkup, e);
        }
    }

    @Override
    public void forwardMessage(ForwardMessage forwardMessage) {
        try {
            cowBot.execute(forwardMessage);
        } catch (TelegramApiException e) {
            log.warn("Cannot forward message: {}", forwardMessage, e);
            throw new MessageException("Cannot forward message: " + forwardMessage, e);
        }
    }

    @Override
    public void deleteMessage(DeleteMessage deleteMessage) {
        try {
            cowBot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.warn("Cannot delete message: {}", deleteMessage, e);
            throw new MessageException("Cannot delete message: " + deleteMessage, e);
        }
    }

    @Override
    public void deleteMessage(Message messageToDelete) {
        deleteMessage(DeleteMessage.builder()
                .chatId(messageToDelete.getChatId())
                .messageId(messageToDelete.getMessageId())
                .build());
    }

    @Override
    public void answerCallbackQuery(AnswerCallbackQuery answerCallbackQuery) {
        try {
            cowBot.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            log.warn("Cannot answer callback query: {}", answerCallbackQuery, e);
            throw new CallbackQueryException("Cannot answer callback query: " + answerCallbackQuery, e);
        }
    }
}
