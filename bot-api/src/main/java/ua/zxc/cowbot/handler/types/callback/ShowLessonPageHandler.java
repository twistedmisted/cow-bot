package ua.zxc.cowbot.handler.types.callback;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.LessonService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.utils.ParseData;

import static ua.zxc.cowbot.Keyboard.createKeyboardForLessons;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_PAGE_SIZE;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.LESSON_LIST_TITLE;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

@Handler(value = "SHOW_LESSON_PAGE", commands = "show_lesson_page_")
@Slf4j
public class ShowLessonPageHandler implements HandlerStrategy {

    private final LessonService lessonService;

    private final TelegramService telegramService;

    public ShowLessonPageHandler(LessonService lessonService, TelegramService telegramService) {
        this.lessonService = lessonService;
        this.telegramService = telegramService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try {
            printLessons(callbackQuery);
        } catch (RuntimeException e) {
            log.warn("Cannot show lesson page, something went wrong", e);
            telegramService.answerCallbackQuery(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "відобразити сторінку з предметами"))
                    .build()
            );
        }
        return new SendMessage();
    }

    private void printLessons(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer pageNumber = ParseData.parseIntAfterLastUnderscore(callbackQuery.getData());
        PageBO<LessonDTO> lessonPage = lessonService.getAllByChatId(chatId, pageNumber, DEFAULT_PAGE_SIZE);
        if (lessonPage.isEmpty()) {
            throw new RuntimeException("Cannot open lesson page: [pageNumber = '" + pageNumber + "']");
        }
        editMessageToPrintLessons(callbackQuery, lessonPage);
    }

    private void editMessageToPrintLessons(CallbackQuery callbackQuery, PageBO<LessonDTO> lessonPage) {
        telegramService.editMessageText(EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(LESSON_LIST_TITLE)
                .parseMode(HTML)
                .replyMarkup(createKeyboardForLessons(lessonPage))
                .build()
        );
    }
}
