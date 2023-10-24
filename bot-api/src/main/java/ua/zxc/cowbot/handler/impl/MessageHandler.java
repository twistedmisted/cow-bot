package ua.zxc.cowbot.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.handler.Handler;
import ua.zxc.cowbot.handler.HandlerFactory;

@Component
@RequiredArgsConstructor
public class MessageHandler implements Handler {

    private final HandlerFactory handlerFactory;

    @Override
    public SendMessage createSendMessage(Update update) {
        handlerFactory.setRawCommand(update.getMessage().getText());
        return handlerFactory.getMessageHandler().handleMessage(update);
    }
}
