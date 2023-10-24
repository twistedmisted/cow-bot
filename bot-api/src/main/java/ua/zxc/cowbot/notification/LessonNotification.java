package ua.zxc.cowbot.notification;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.service.TelegramService;

import static ua.zxc.cowbot.utils.Constants.HTML;

@Component
public class LessonNotification {

    private final TelegramService telegramService;

    public LessonNotification(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    public void sendAddedNotification(LessonDTO lesson) {
        telegramService.sendMessage(SendMessage.builder()
                .chatId(lesson.getChatId())
                .text("<b>Предмет '" + lesson.getName() + "' успішно додано.</b>")
                .parseMode(HTML)
                .build()
        );
    }

    public void sendUpdatedNotification(LessonDTO lesson) {
        telegramService.sendMessage(SendMessage.builder()
                .chatId(lesson.getChatId())
                .text("<b>Предмет '" + lesson.getName() + "' успішно оновлено.</b>")
                .parseMode(HTML)
                .build()
        );
    }
}
