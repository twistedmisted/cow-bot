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
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.helper.QueueInitializr;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.QueueService;
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
import static ua.zxc.cowbot.utils.Constants.QUEUE_LIST_TITLE;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

class ShowQueuePageHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE =
            TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText("show_queue_page_2");

    @Autowired
    private ShowQueuePageHandler showQueuePageHandler;

    @MockBean
    private QueueService queueService;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageText> captorEditMessageText;

    @Captor
    private ArgumentCaptor<AnswerCallbackQuery> captorAnswerCallbackQuery;

    @Test
    public void handleMessageWithExistingQueuePageShouldReturnMessageWithLessonPage() {
        QueueDTO queueDTO = QueueInitializr.createDTO();

        when(queueService.getAllByChatId(anyLong(), anyInt(), anyInt()))
                .thenReturn(PageBO.<QueueDTO>builder()
                        .totalPages(2)
                        .currentPageNumber(1)
                        .content(Collections.nCopies(5, queueDTO))
                        .build());

        showQueuePageHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).editMessageText(captorEditMessageText.capture());

        EditMessageText actualEditMessage = captorEditMessageText.getValue();

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualEditMessage.getChatId()));
        assertEquals(HTML, actualEditMessage.getParseMode());
        assertEquals(QUEUE_LIST_TITLE, actualEditMessage.getText());
        assertNotNull(actualEditMessage.getReplyMarkup());

        InlineKeyboardMarkup actualInlineKeyboardMarkup = actualEditMessage.getReplyMarkup();
        List<List<InlineKeyboardButton>> actualKeyboard = actualInlineKeyboardMarkup.getKeyboard();

        assertEquals(7, actualKeyboard.size());

        InlineKeyboardButton firstQueueButton = actualKeyboard.get(0).get(0);

        assertEquals(queueDTO.getName(), firstQueueButton.getText());
        assertEquals("show_places_for_queue_by_id_" + queueDTO.getId(), firstQueueButton.getCallbackData());

        List<InlineKeyboardButton> paginationButtons = actualKeyboard.get(5);

        assertEquals("show_queue_page_0", paginationButtons.get(0).getCallbackData());
        assertEquals("2/2", paginationButtons.get(1).getText());
    }

    @Test
    public void handleMessageWithNotExistingQueuePageShouldReturnMessageWithLessonPage() {
        when(queueService.getAllByChatId(anyLong(), anyInt(), anyInt()))
                .thenReturn(new PageBO<>());

        showQueuePageHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).answerCallbackQuery(captorAnswerCallbackQuery.capture());

        AnswerCallbackQuery actualAnswerCallbackQuery = captorAnswerCallbackQuery.getValue();

        assertEquals(UPDATE.getCallbackQuery().getId(), actualAnswerCallbackQuery.getCallbackQueryId());
        assertEquals(String.format(TRY_AGAIN_WHAT_SOMETHING, "відобразити сторінку з чергами"),
                actualAnswerCallbackQuery.getText());
    }
}