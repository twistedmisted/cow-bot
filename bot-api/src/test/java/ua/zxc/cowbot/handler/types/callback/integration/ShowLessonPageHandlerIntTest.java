package ua.zxc.cowbot.handler.types.callback.integration;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.handler.types.callback.ShowLessonPageHandler;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.LessonService;
import ua.zxc.cowbot.service.TelegramService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_PAGE_SIZE;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.LESSON_LIST_TITLE;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

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
public class ShowLessonPageHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private ShowLessonPageHandler showLessonPageHandler;

    @Autowired
    private LessonService lessonService;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageText> captorEditMessageText;

    @Captor
    private ArgumentCaptor<AnswerCallbackQuery> captorAnswerCallbackQuery;

    @Test
    public void handleMessageWithExistingLessonPageShouldReturnMessageWithLessonPage() {
        Long chatId = 1L;
        Integer pageNumber = 1;
        Update update = TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText(chatId, "show_lesson_page_" + pageNumber);

        showLessonPageHandler.handleMessage(update);

        verify(telegramService, times(1)).editMessageText(captorEditMessageText.capture());

        EditMessageText actualEditMessage = captorEditMessageText.getValue();

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualEditMessage.getChatId()));
        assertEquals(HTML, actualEditMessage.getParseMode());
        assertEquals(LESSON_LIST_TITLE, actualEditMessage.getText());
        assertNotNull(actualEditMessage.getReplyMarkup());

        InlineKeyboardMarkup actualInlineKeyboardMarkup = actualEditMessage.getReplyMarkup();
        List<List<InlineKeyboardButton>> actualKeyboard = actualInlineKeyboardMarkup.getKeyboard();

        PageBO<LessonDTO> lessonPage = lessonService.getAllByChatId(chatId, pageNumber, DEFAULT_PAGE_SIZE);
        List<LessonDTO> lessonsByChatId = lessonPage.getContent();
        LessonDTO lessonDTO = lessonsByChatId.get(0);

        assertEquals(lessonsByChatId.size() + 2, actualKeyboard.size());

        InlineKeyboardButton firstLessonButton = actualKeyboard.get(0).get(0);

        assertEquals(lessonDTO.getName(), firstLessonButton.getText());
        assertEquals("show_lesson_by_id_" + lessonDTO.getId(), firstLessonButton.getCallbackData());

        List<InlineKeyboardButton> paginationButtons = actualKeyboard.get(actualKeyboard.size() - 2);

        assertEquals("show_lesson_page_0", paginationButtons.get(0).getCallbackData());
        assertEquals("2/2", paginationButtons.get(1).getText());
    }

    @Test
    public void handleMessageWithNotExistingLessonPageShouldReturnMessageWithLessonPage() {
        Long chatId = 1L;
        int pageNumber = 4;
        Update update = TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText(chatId, "show_lesson_page_" + pageNumber);

        showLessonPageHandler.handleMessage(update);

        verify(telegramService, times(1)).answerCallbackQuery(captorAnswerCallbackQuery.capture());

        AnswerCallbackQuery actualAnswerCallbackQuery = captorAnswerCallbackQuery.getValue();

        assertEquals(update.getCallbackQuery().getId(), actualAnswerCallbackQuery.getCallbackQueryId());
        assertEquals(String.format(TRY_AGAIN_WHAT_SOMETHING, "відобразити сторінку з предметами"),
                actualAnswerCallbackQuery.getText());
    }
}