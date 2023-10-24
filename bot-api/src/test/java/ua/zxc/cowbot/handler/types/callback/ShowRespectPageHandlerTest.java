package ua.zxc.cowbot.handler.types.callback;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.RespectDTO;
import ua.zxc.cowbot.helper.RespectInitializr;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.RespectService;
import ua.zxc.cowbot.service.TelegramService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.RESPECT_LIST_TITLE;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

class ShowRespectPageHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE =
            TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText("show_lesson_page_1");

    @Autowired
    private ShowRespectPageHandler showRespectPageHandler;

    @MockBean
    private RespectService respectService;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageText> captorEditMessageText;

    @Captor
    private ArgumentCaptor<AnswerCallbackQuery> captorAnswerCallbackQuery;

    @Test
    public void handleMessageWithExistingLessonPageShouldReturnMessageWithLessonPage() {
        RespectDTO respectDTO = RespectInitializr.createDTO();

        when(respectService.getAllByChatId(anyLong(), anyInt(), anyInt()))
                .thenReturn(PageBO.<RespectDTO>builder()
                        .totalPages(2)
                        .currentPageNumber(1)
                        .content(Collections.nCopies(10, respectDTO))
                        .build());

        showRespectPageHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).editMessageText(captorEditMessageText.capture());

        EditMessageText actualEditMessage = captorEditMessageText.getValue();

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualEditMessage.getChatId()));
        assertEquals(HTML, actualEditMessage.getParseMode());
        assertEquals(
                RESPECT_LIST_TITLE + System.lineSeparator() +
                        "<b>1. Firstname:</b> <i>10 впод.</i>" + System.lineSeparator() +
                        "<b>2. Firstname:</b> <i>10 впод.</i>" + System.lineSeparator() +
                        "<b>3. Firstname:</b> <i>10 впод.</i>" + System.lineSeparator() +
                        "<b>4. Firstname:</b> <i>10 впод.</i>" + System.lineSeparator() +
                        "<b>5. Firstname:</b> <i>10 впод.</i>" + System.lineSeparator() +
                        "<b>6. Firstname:</b> <i>10 впод.</i>" + System.lineSeparator() +
                        "<b>7. Firstname:</b> <i>10 впод.</i>" + System.lineSeparator() +
                        "<b>8. Firstname:</b> <i>10 впод.</i>" + System.lineSeparator() +
                        "<b>9. Firstname:</b> <i>10 впод.</i>" + System.lineSeparator() +
                        "<b>10. Firstname:</b> <i>10 впод.</i>" + System.lineSeparator(),
                actualEditMessage.getText());
        assertNotNull(actualEditMessage.getReplyMarkup());

        InlineKeyboardMarkup actualInlineKeyboardMarkup = actualEditMessage.getReplyMarkup();
        List<List<InlineKeyboardButton>> actualKeyboard = actualInlineKeyboardMarkup.getKeyboard();

        assertEquals(2, actualKeyboard.size());

        List<InlineKeyboardButton> paginationButtons = actualKeyboard.get(0);

        assertEquals("show_respect_page_0", paginationButtons.get(0).getCallbackData());
        assertEquals("2/2", paginationButtons.get(1).getText());
    }

    @Test
    public void handleMessageWithNotExistingLessonPageShouldReturnMessageWithLessonPage() {
        when(respectService.getAllByChatId(anyLong(), anyInt(), anyInt()))
                .thenReturn(new PageBO<>());

        showRespectPageHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).answerCallbackQuery(captorAnswerCallbackQuery.capture());

        AnswerCallbackQuery actualAnswerCallbackQuery = captorAnswerCallbackQuery.getValue();

        assertEquals(UPDATE.getCallbackQuery().getId(), actualAnswerCallbackQuery.getCallbackQueryId());
        assertEquals(String.format(TRY_AGAIN_WHAT_SOMETHING, "відобразити сторінку з вподобайками"),
                actualAnswerCallbackQuery.getText());
    }
}