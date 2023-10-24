package ua.zxc.cowbot.service;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface TelegramService {

    void sendMessage(SendMessage sendMessage);

    void editMessageText(EditMessageText editMessageText);

    void editMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup);

    void forwardMessage(ForwardMessage forwardMessage);

    void deleteMessage(DeleteMessage deleteMessage);

    void deleteMessage(Message messageToDelete);

    void answerCallbackQuery(AnswerCallbackQuery answerCallbackQuery);
}
