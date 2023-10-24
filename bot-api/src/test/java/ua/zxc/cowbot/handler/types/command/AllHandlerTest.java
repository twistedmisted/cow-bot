package ua.zxc.cowbot.handler.types.command;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.service.UserService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.zxc.cowbot.helper.UserInitializr.createUserDTO;
import static ua.zxc.cowbot.utils.Constants.HTML;

class AllHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE = TelegramTestHelper.createMessageUpdateFromGroupChatWithMessageText("/all");

    @Captor
    private ArgumentCaptor<SendMessage> captor;

    @Autowired
    private AllHandler allHandler;

    @MockBean
    private UserService userService;

    @MockBean
    private TelegramService telegramService;

    @Test
    public void handleMessageWithUpdateShouldAndRegisteredUsersShouldMentionedUsers() {
        when(userService.getAllByChatId(anyLong(), eq(0), eq(5)))
                .thenReturn(Collections.nCopies(5, createUserDTO()));
        when(userService.getAllByChatId(anyLong(), eq(1), eq(5)))
                .thenReturn(Collections.nCopies(1, createUserDTO()));
        when(userService.getAllByChatId(anyLong(), eq(2), eq(5)))
                .thenReturn(Collections.emptyList());
        doNothing().when(telegramService).sendMessage(any(SendMessage.class));

        allHandler.handleMessage(UPDATE);

        verify(telegramService, times(2)).sendMessage(captor.capture());

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(captor.getAllValues().get(0).getChatId()));
        assertEquals(HTML, captor.getAllValues().get(0).getParseMode());
        assertEquals(captor.getAllValues().get(0).getText(),
                "<a href=\"tg://user?id=1\">Firstname</a>, " +
                        "<a href=\"tg://user?id=1\">Firstname</a>, " +
                        "<a href=\"tg://user?id=1\">Firstname</a>, " +
                        "<a href=\"tg://user?id=1\">Firstname</a>, " +
                        "<a href=\"tg://user?id=1\">Firstname</a>");
        assertEquals(captor.getAllValues().get(1).getText(), "<a href=\"tg://user?id=1\">Firstname</a>");
    }

    @Test
    public void handleMessageWithUpdateShouldAndWithoutRegisteredUsersShouldMentionedUsers() {
        when(userService.getAllByChatId(anyLong(), eq(0), eq(5)))
                .thenReturn(Collections.nCopies(5, createUserDTO()));
        when(userService.getAllByChatId(anyLong(), eq(1), eq(5)))
                .thenReturn(Collections.nCopies(1, createUserDTO()));
        when(userService.getAllByChatId(anyLong(), eq(2), eq(5)))
                .thenReturn(Collections.emptyList());
        doNothing().when(telegramService).sendMessage(any(SendMessage.class));

        allHandler.handleMessage(UPDATE);

        verify(telegramService, times(2)).sendMessage(captor.capture());

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(captor.getAllValues().get(0).getChatId()));
        assertEquals(HTML, captor.getAllValues().get(0).getParseMode());
        assertEquals(captor.getAllValues().get(0).getText(),
                "<a href=\"tg://user?id=1\">Firstname</a>, " +
                        "<a href=\"tg://user?id=1\">Firstname</a>, " +
                        "<a href=\"tg://user?id=1\">Firstname</a>, " +
                        "<a href=\"tg://user?id=1\">Firstname</a>, " +
                        "<a href=\"tg://user?id=1\">Firstname</a>");
        assertEquals(captor.getAllValues().get(1).getText(), "<a href=\"tg://user?id=1\">Firstname</a>");
    }
}