package ua.zxc.cowbot.handler.types.command;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.helper.QueueInitializr;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.QueueService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.NOTHING_TO_SHOW;
import static ua.zxc.cowbot.utils.Constants.QUEUE_LIST_TITLE;

public class QueuesHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE = TelegramTestHelper.createMessageUpdateFromGroupChat();

    @Autowired
    private QueuesHandler queuesHandler;

    @MockBean
    private QueueService queueService;

    @Test
    public void handleMessageWithExistingQueuesByChatIdShouldReturnMessageWithQueues() {
        QueueDTO queueDTO = QueueInitializr.createDTO();
        when(queueService.getAllByChatId(anyLong(), anyInt(), anyInt()))
                .thenReturn(PageBO.<QueueDTO>builder()
                        .totalPages(2)
                        .currentPageNumber(0)
                        .content(Collections.nCopies(5, queueDTO))
                        .build());

        SendMessage actualSendMessage = queuesHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(QUEUE_LIST_TITLE, actualSendMessage.getText());
        assertNotNull(actualSendMessage.getReplyMarkup());

        InlineKeyboardMarkup actualInlineKeyboardMarkup = (InlineKeyboardMarkup) actualSendMessage.getReplyMarkup();
        List<List<InlineKeyboardButton>> actualKeyboard = actualInlineKeyboardMarkup.getKeyboard();

        assertEquals(7, actualKeyboard.size());

        InlineKeyboardButton firstLessonButton = actualKeyboard.get(0).get(0);

        assertEquals(queueDTO.getName(), firstLessonButton.getText());
        assertEquals("show_places_for_queue_by_id_" + queueDTO.getId(), firstLessonButton.getCallbackData());

        List<InlineKeyboardButton> paginationButtons = actualKeyboard.get(5);

        assertEquals("1/2", paginationButtons.get(0).getText());
        assertEquals("show_queue_page_1", paginationButtons.get(1).getCallbackData());
    }

    @Test
    public void handleMessageWithNoExistingLessonsByChatIdShouldReturnMessageWithLessons() {
        when(queueService.getAllByChatId(anyLong(), anyInt(), anyInt()))
                .thenReturn(new PageBO<>());

        SendMessage actualSendMessage = queuesHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(NOTHING_TO_SHOW, actualSendMessage.getText());
        assertNull(actualSendMessage.getReplyMarkup());
    }
}