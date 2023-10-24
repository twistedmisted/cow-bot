package ua.zxc.cowbot.handler.types.command;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.TelegramService;

import static ua.zxc.cowbot.Keyboard.createKeyboardForHelpMessage;
import static ua.zxc.cowbot.utils.Constants.HELP;
import static ua.zxc.cowbot.utils.Constants.HTML;

@Handler(value = "HELP", commands = "/help")
@RequiredArgsConstructor
public class HelpHandler implements HandlerStrategy {

    private final TelegramService telegramService;

    @Override
    public SendMessage handleMessage(Update update) {
        telegramService.deleteMessage(update.getMessage());
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(HELP)
                .parseMode(HTML)
                .replyMarkup(createKeyboardForHelpMessage())
                .build();
    }
}
