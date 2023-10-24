package ua.zxc.cowbot.handler.types.command.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.handler.types.command.SetGroupNameHandler;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.ChatService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ua.zxc.cowbot.utils.Constants.GROUP_NAME_UPDATED;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.CHAT_NEED_REGISTRATION;
import static ua.zxc.cowbot.utils.Constants.SPECIFY_GROUP_NAME;

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
public class SetGroupNameHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private SetGroupNameHandler setGroupNameHandler;

    @Autowired
    private ChatService chatService;

    @Test
    public void handleMessageWithExistingChatShouldReturnGroupNameUpdatedMessage() {
        Long chatId = 1L;
        String groupName = "XX-00";
        Update update = TelegramTestHelper.createMessageUpdateFromGroupChatWithMessageText(chatId, "/g " + groupName);

        SendMessage actualSendMessage = setGroupNameHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(String.format(GROUP_NAME_UPDATED, groupName), actualSendMessage.getText());

        ChatDTO chatById = chatService.getChatById(update.getMessage().getChatId());

        assertEquals(groupName, chatById.getGroupName());
    }

    @Test
    public void handleMessageWithNotExistingChatShouldReturnNeedRegistrationMessage() {
        Long chatId = 10000L;
        String groupName = "XX-00";
        Update update = TelegramTestHelper.createMessageUpdateFromGroupChatWithMessageText(chatId, "/g " + groupName);

        SendMessage actualSendMessage = setGroupNameHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(CHAT_NEED_REGISTRATION, actualSendMessage.getText());

        ChatDTO chatById = chatService.getChatById(update.getMessage().getChatId());

        assertNull(chatById);
    }

    @Test
    public void handleMessageWithoutGroupNameShouldReturnSpecifyGroupNameMessage() {
        Long chatId = 1L;
        Update update = TelegramTestHelper.createMessageUpdateFromGroupChatWithMessageText(chatId, "/g");

        SendMessage actualSendMessage = setGroupNameHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(SPECIFY_GROUP_NAME, actualSendMessage.getText());
    }
}