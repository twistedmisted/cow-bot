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
import ua.zxc.cowbot.dto.RespectDTO;
import ua.zxc.cowbot.handler.types.callback.ShowRespectPageHandler;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.mapper.impl.RespectMapper;
import ua.zxc.cowbot.service.RespectService;
import ua.zxc.cowbot.service.TelegramService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_PAGE_SIZE_FOR_RESPECTS;
import static ua.zxc.cowbot.utils.Constants.HTML;
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
class ShowRespectPageHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private ShowRespectPageHandler showRespectPageHandler;

    @Autowired
    private RespectService respectService;

    @Autowired
    private RespectMapper respectMapper;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageText> captorEditMessageText;

    @Captor
    private ArgumentCaptor<AnswerCallbackQuery> captorAnswerCallbackQuery;

    @Test
    public void handleMessageWithExistingRespectPageShouldReturnMessageWithLessonPage() {
        Long chatId = 1L;
        Integer pageNumber = 1;
        Update update = TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText(chatId, "show_lesson_page_" + pageNumber);

        showRespectPageHandler.handleMessage(update);

        verify(telegramService, times(1)).editMessageText(captorEditMessageText.capture());

        PageBO<RespectDTO> allByChatId = respectService.getAllByChatId(chatId, pageNumber, DEFAULT_PAGE_SIZE_FOR_RESPECTS);

        EditMessageText actualEditMessage = captorEditMessageText.getValue();

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualEditMessage.getChatId()));
        assertEquals(HTML, actualEditMessage.getParseMode());
        assertEquals(respectMapper.dtosToString(allByChatId.getContent()), actualEditMessage.getText());
        assertNotNull(actualEditMessage.getReplyMarkup());

        InlineKeyboardMarkup actualInlineKeyboardMarkup = actualEditMessage.getReplyMarkup();
        List<List<InlineKeyboardButton>> actualKeyboard = actualInlineKeyboardMarkup.getKeyboard();

        assertEquals(2, actualKeyboard.size());

        List<InlineKeyboardButton> paginationButtons = actualKeyboard.get(0);

        assertEquals("show_respect_page_0", paginationButtons.get(0).getCallbackData());
        assertEquals("2/2", paginationButtons.get(1).getText());
    }

    @Test
    public void handleMessageWithNotExistingRespectPageShouldReturnMessageWithLessonPage() {
        Long chatId = 1L;
        Integer pageNumber = 1000;
        Update update = TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText(chatId, "show_lesson_page_" + pageNumber);

        showRespectPageHandler.handleMessage(update);

        verify(telegramService, times(1)).answerCallbackQuery(captorAnswerCallbackQuery.capture());

        AnswerCallbackQuery actualAnswerCallbackQuery = captorAnswerCallbackQuery.getValue();

        assertEquals(update.getCallbackQuery().getId(), actualAnswerCallbackQuery.getCallbackQueryId());
        assertEquals(String.format(TRY_AGAIN_WHAT_SOMETHING, "відобразити сторінку з вподобайками"),
                actualAnswerCallbackQuery.getText());
    }
}