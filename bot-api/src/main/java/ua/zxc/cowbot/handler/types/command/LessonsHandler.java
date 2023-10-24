package ua.zxc.cowbot.handler.types.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.LessonService;
import ua.zxc.cowbot.service.TelegramService;

import static ua.zxc.cowbot.Keyboard.createKeyboardForLessons;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_NUMBER_PAGE;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_PAGE_SIZE;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.LESSON_LIST_TITLE;
import static ua.zxc.cowbot.utils.Constants.NOTHING_TO_SHOW;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

@Handler(value = "LESSONS", commands = "/lessons")
@Slf4j
@RequiredArgsConstructor
public class LessonsHandler implements HandlerStrategy {

    private final LessonService lessonService;
    private final TelegramService telegramService;

    @Override
    public SendMessage handleMessage(Update update) {
        telegramService.deleteMessage(update.getMessage());
        try {
            return printLessons(update);
        } catch (RuntimeException e) {
            Long chatId = update.getMessage().getChatId();
            log.warn("Cannot print lessons in chat with id: {}", chatId, e);
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "вивести список предметів"))
                    .parseMode(HTML)
                    .build();
        }
    }

    private SendMessage printLessons(Update update) {
        Long chatId = update.getMessage().getChatId();
        PageBO<LessonDTO> lessonPage = lessonService.getAllByChatId(chatId, DEFAULT_NUMBER_PAGE, DEFAULT_PAGE_SIZE);
        return createMessageForLessons(lessonPage, chatId);
    }

    private SendMessage createMessageForLessons(PageBO<LessonDTO> lessonPage, Long chatId) {
        boolean pageEmpty = lessonPage.isEmpty();
        SendMessage messageToSend = SendMessage.builder()
                .chatId(chatId)
                .text(getMessageText(pageEmpty))
                .parseMode(HTML)
                .build();
        if (!pageEmpty) {
            messageToSend.setReplyMarkup(createKeyboardForLessons(lessonPage));
        }
        return messageToSend;
    }

    private String getMessageText(boolean empty) {
        if (empty) {
            return NOTHING_TO_SHOW;
        }
        return LESSON_LIST_TITLE;
    }
}
