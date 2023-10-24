package ua.zxc.cowbot.handler.types.command.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.handler.types.command.LessonsHandler;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.LessonService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_NUMBER_PAGE;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_PAGE_SIZE;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.LESSON_LIST_TITLE;
import static ua.zxc.cowbot.utils.Constants.NOTHING_TO_SHOW;

@Sql(
        value = {
                "classpath:/db-scripts/clear-tables.sql",
                "classpath:/db-scripts/populate-tables.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(
        value = {
                "classpath:/db-scripts/clear-tables.sql"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LessonsHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private LessonsHandler lessonsHandler;

    @Autowired
    private LessonService lessonService;

    @Test
    public void handleMessageWithExistingLessonsByChatIdShouldReturnMessageWithLessons() {
        Long chatId = 1L;
        Update update = TelegramTestHelper.createMessageUpdateFromGroupChatWithMessageText(chatId);

        SendMessage actualSendMessage = lessonsHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(LESSON_LIST_TITLE, actualSendMessage.getText());
        assertNotNull(actualSendMessage.getReplyMarkup());

        InlineKeyboardMarkup actualInlineKeyboardMarkup = (InlineKeyboardMarkup) actualSendMessage.getReplyMarkup();
        List<List<InlineKeyboardButton>> actualKeyboard = actualInlineKeyboardMarkup.getKeyboard();

        PageBO<LessonDTO> lessonPage = lessonService.getAllByChatId(chatId, DEFAULT_NUMBER_PAGE, DEFAULT_PAGE_SIZE);
        List<LessonDTO> lessonsByChatId = lessonPage.getContent();
        LessonDTO lessonDTO = lessonsByChatId.get(0);

        assertEquals(lessonsByChatId.size() + 2, actualKeyboard.size());

        InlineKeyboardButton firstLessonButton = actualKeyboard.get(0).get(0);

        assertEquals(lessonDTO.getName(), firstLessonButton.getText());
        assertEquals("show_lesson_by_id_" + lessonDTO.getId(), firstLessonButton.getCallbackData());

        List<InlineKeyboardButton> paginationButtons = actualKeyboard.get(actualKeyboard.size() - 2);

        assertEquals("1/2", paginationButtons.get(0).getText());
        assertEquals("show_lesson_page_1", paginationButtons.get(1).getCallbackData());
    }

    @Test
    public void handleMessageWithNoExistingLessonsByChatIdShouldReturnMessageWithLessons() {
        Update update = TelegramTestHelper.createMessageUpdateFromGroupNameWithChatId(999L);

        SendMessage actualSendMessage = lessonsHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(NOTHING_TO_SHOW, actualSendMessage.getText());
        assertNull(actualSendMessage.getReplyMarkup());
    }
}
