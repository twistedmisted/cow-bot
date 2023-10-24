package ua.zxc.cowbot.handler.types.command;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.TelegramService;

import static ua.zxc.cowbot.Keyboard.createKeyboardForAddLessonMessage;
import static ua.zxc.cowbot.utils.Constants.ADD_LESSON;
import static ua.zxc.cowbot.utils.Constants.HTML;

@RequiredArgsConstructor
@Handler(value = "ADD_LESSON", commands = "/addlesson")
public class AddLessonHandler implements HandlerStrategy {

    private final TelegramService telegramService;

    @Override
    public SendMessage handleMessage(Update update) {
        telegramService.deleteMessage(update.getMessage());
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(ADD_LESSON)
                .parseMode(HTML)
                .replyMarkup(createKeyboardForAddLessonMessage())
                .build();
    }
}
