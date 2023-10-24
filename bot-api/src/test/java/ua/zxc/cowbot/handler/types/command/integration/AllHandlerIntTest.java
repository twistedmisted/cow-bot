package ua.zxc.cowbot.handler.types.command.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.handler.types.command.AllHandler;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.TelegramService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ua.zxc.cowbot.utils.Constants.OPERATION_SUCCESSFUL;

public class AllHandlerIntTest extends BotApiSpringBootTest {

    private static final Update UPDATE = TelegramTestHelper.createMessageUpdateFromGroupChatWithMessageText(1L);

    @Autowired
    private AllHandler allHandler;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<SendMessage> captor;

    @BeforeEach
    public void setUp() {
        doNothing().when(telegramService).sendMessage(any(SendMessage.class));
    }

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
    @Test
    public void handleMessageWithUpdateAndRegisteredUsersShouldMentionedUsers() {
        SendMessage actualSendMessage = allHandler.handleMessage(UPDATE);

        verify(telegramService, times(2)).sendMessage(captor.capture());

        assertEquals(OPERATION_SUCCESSFUL, actualSendMessage.getText());
        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));

        assertEquals("<a href=\"tg://user?id=1\">First name</a>, " +
                "<a href=\"tg://user?id=2\">Second name</a>, " +
                "<a href=\"tg://user?id=3\">Third name</a>, " +
                "<a href=\"tg://user?id=4\">Fourth name</a>, " +
                "<a href=\"tg://user?id=5\">Fifth name</a>", captor.getAllValues().get(0).getText());
        assertEquals("<a href=\"tg://user?id=6\">Sixth name</a>, " +
                "<a href=\"tg://user?id=7\">Seventh name</a>", captor.getAllValues().get(1).getText());
    }

    @Test
    public void handleMessageWithUpdateAndZeroRegisteredUsersShouldSendMessage() {
        SendMessage actualSendMessage = allHandler.handleMessage(UPDATE);

        verify(telegramService, times(0)).sendMessage(any(SendMessage.class));

        assertEquals(OPERATION_SUCCESSFUL, actualSendMessage.getText());
        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
    }
}
