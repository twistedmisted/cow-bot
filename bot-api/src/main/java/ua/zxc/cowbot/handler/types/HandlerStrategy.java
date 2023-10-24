package ua.zxc.cowbot.handler.types;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface HandlerStrategy {

    // TODO: think about abstract class to add default method to create SendMessage

    SendMessage handleMessage(Update update);
}
