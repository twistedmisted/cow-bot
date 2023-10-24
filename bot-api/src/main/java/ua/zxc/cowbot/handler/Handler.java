package ua.zxc.cowbot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Handler {

    SendMessage createSendMessage(Update update);
}
