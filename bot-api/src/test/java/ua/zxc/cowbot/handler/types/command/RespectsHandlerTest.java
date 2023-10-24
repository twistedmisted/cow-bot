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
import ua.zxc.cowbot.dto.RespectDTO;
import ua.zxc.cowbot.helper.RespectInitializr;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.RespectService;

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
import static ua.zxc.cowbot.utils.Constants.RESPECT_LIST_TITLE;

public class RespectsHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE = TelegramTestHelper.createMessageUpdateFromGroupChat();

    @Autowired
    private RespectsHandler respectsHandler;

    @MockBean
    private RespectService respectService;

    @Test
    public void handlerMessageWithNotEmptyRespectListShouldReturnMessageWithList() {
        RespectDTO respectDTO = RespectInitializr.createDTO();

        when(respectService.getAllByChatId(anyLong(), anyInt(), anyInt()))
                .thenReturn(PageBO.<RespectDTO>builder()
                        .totalPages(2)
                        .currentPageNumber(0)
                        .content(Collections.nCopies(10, respectDTO))
                        .build());

        SendMessage actualSendMessage = respectsHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
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
                actualSendMessage.getText());
        assertNotNull(actualSendMessage.getReplyMarkup());

        InlineKeyboardMarkup actualInlineKeyboardMarkup = (InlineKeyboardMarkup) actualSendMessage.getReplyMarkup();
        List<List<InlineKeyboardButton>> actualKeyboard = actualInlineKeyboardMarkup.getKeyboard();

        assertEquals(2, actualKeyboard.size());

        List<InlineKeyboardButton> paginationButtons = actualKeyboard.get(0);

        assertEquals("1/2", paginationButtons.get(0).getText());
        assertEquals("show_respect_page_1", paginationButtons.get(1).getCallbackData());
    }

    @Test
    public void handlerMessageWithEmptyRespectListShouldReturnMessageWithList() {
        when(respectService.getAllByChatId(anyLong(), anyInt(), anyInt()))
                .thenReturn(new PageBO<>());

        SendMessage actualSendMessage = respectsHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(NOTHING_TO_SHOW, actualSendMessage.getText());
        assertNull(actualSendMessage.getReplyMarkup());
    }
}
