package ua.zxc.cowbot.handler.types.callback;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.TelegramService;

@Handler(value = "CLOSE", commands = "close")
@Slf4j
public class CloseHandler implements HandlerStrategy {

    private final TelegramService telegramService;

    public CloseHandler(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        try {
            telegramService.deleteMessage(DeleteMessage.builder()
                    .chatId(update.getCallbackQuery().getMessage().getChatId())
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .build()
            );
        } catch (RuntimeException e) {
            log.warn("Cannot close message", e);
        }
        return new SendMessage();
    }
}
