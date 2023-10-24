package ua.zxc.cowbot.handler.impl;

import ua.zxc.cowbot.handler.Handler;
import ua.zxc.cowbot.handler.HandlerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CallbackHandler implements Handler {

    private final HandlerFactory handlerFactory;

    public CallbackHandler(HandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    @Override
    public SendMessage createSendMessage(Update update) {
        handlerFactory.setRawCommand(update.getCallbackQuery().getData());
        SendMessage sendMessage = handlerFactory.getCallbackHandler().handleMessage(update);
        sendMessage.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
        return sendMessage;
    }
}