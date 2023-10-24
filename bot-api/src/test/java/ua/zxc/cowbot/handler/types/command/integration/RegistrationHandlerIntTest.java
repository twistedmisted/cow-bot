package ua.zxc.cowbot.handler.types.command.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.handler.types.command.RegistrationHandler;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.ChatService;
import ua.zxc.cowbot.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.SUCCESSFUL_REGISTRATION;
import static ua.zxc.cowbot.utils.Constants.USER_ALREADY_REGISTERED;

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
public class RegistrationHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private RegistrationHandler registrationHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Test
    public void handleMessageWithNotExistingUserAndNotExistingChatShouldReturnSuccessfulRegistrationMessage() {
        Long chatId = 20L;
        Long userId = 20L;
        Update update = TelegramTestHelper.createMessageUpdateFromGroupNameWithChatIdAndUserId(chatId, userId);

        assertFalse(userService.existsUserById(userId));
        assertFalse(chatService.existsById(chatId));

        List<UserDTO> usersByChatId = userService.getAllByChatId(chatId);
        int usersByChatIdListSizeBeforeRegistration = usersByChatId.size();

        assertTrue(usersByChatId.isEmpty());

        List<ChatDTO> chatsByUserId = chatService.getAllByUsersId(userId);
        int chatsByUserIdListSizeBeforeRegistration = chatsByUserId.size();

        assertTrue(chatsByUserId.isEmpty());

        SendMessage actualSendMessage = registrationHandler.handleMessage(update);

        assertEquals(chatId, Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(SUCCESSFUL_REGISTRATION, actualSendMessage.getText());

        assertTrue(userService.existsUserById(userId));
        assertTrue(chatService.existsById(chatId));

        usersByChatId = userService.getAllByChatId(chatId);
        int usersByChatIdListSizeAfterRegistration = usersByChatId.size();

        assertFalse(userService.getAllByChatId(chatId).isEmpty());
        assertEquals(usersByChatIdListSizeBeforeRegistration + 1, usersByChatIdListSizeAfterRegistration);

        chatsByUserId = chatService.getAllByUsersId(userId);
        int chatsByUserIdListSizeAfterRegistration = chatsByUserId.size();

        assertFalse(chatService.getAllByUsersId(userId).isEmpty());
        assertEquals(chatsByUserIdListSizeBeforeRegistration + 1, chatsByUserIdListSizeAfterRegistration);
    }

    @Test
    public void handleMessageWithNotExistingUserAndExistingChatShouldReturnSuccessfulRegistrationMessage() {
        Long chatId = 4L;
        Long userId = 20L;
        Update update = TelegramTestHelper.createMessageUpdateFromGroupNameWithChatIdAndUserId(chatId, userId);

        assertFalse(userService.existsUserById(userId));
        assertTrue(chatService.existsById(chatId));


        List<UserDTO> usersByChatId = userService.getAllByChatId(chatId);
        int usersByChatIdListSizeBeforeRegistration = usersByChatId.size();

        assertFalse(usersByChatId.isEmpty());

        List<ChatDTO> chatsByUserId = chatService.getAllByUsersId(userId);
        int chatsByUserIdListSizeBeforeRegistration = chatsByUserId.size();

        assertTrue(chatsByUserId.isEmpty());

        SendMessage actualSendMessage = registrationHandler.handleMessage(update);

        assertEquals(chatId, Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(SUCCESSFUL_REGISTRATION, actualSendMessage.getText());

        assertTrue(userService.existsUserById(userId));
        assertTrue(chatService.existsById(chatId));

        usersByChatId = userService.getAllByChatId(chatId);
        int usersByChatIdListSizeAfterRegistration = usersByChatId.size();

        assertFalse(userService.getAllByChatId(chatId).isEmpty());
        assertEquals(usersByChatIdListSizeBeforeRegistration + 1, usersByChatIdListSizeAfterRegistration);

        chatsByUserId = chatService.getAllByUsersId(userId);
        int chatsByUserIdListSizeAfterRegistration = chatsByUserId.size();

        assertFalse(chatService.getAllByUsersId(userId).isEmpty());
        assertEquals(chatsByUserIdListSizeBeforeRegistration + 1, chatsByUserIdListSizeAfterRegistration);
    }

    @Test
    public void handleMessageWithExistingUserAndNotExistingChatShouldReturnSuccessfulRegistrationMessage() {
        Long chatId = 20L;
        Long userId = 1L;
        Update update = TelegramTestHelper.createMessageUpdateFromGroupNameWithChatIdAndUserId(chatId, userId);

        assertTrue(userService.existsUserById(userId));
        assertFalse(chatService.existsById(chatId));


        List<UserDTO> usersByChatId = userService.getAllByChatId(chatId);
        int usersByChatIdListSizeBeforeRegistration = usersByChatId.size();

        assertTrue(usersByChatId.isEmpty());

        List<ChatDTO> chatsByUserId = chatService.getAllByUsersId(userId);
        int chatsByUserIdListSizeBeforeRegistration = chatsByUserId.size();

        assertFalse(chatsByUserId.isEmpty());

        SendMessage actualSendMessage = registrationHandler.handleMessage(update);

        assertEquals(chatId, Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(SUCCESSFUL_REGISTRATION, actualSendMessage.getText());

        assertTrue(userService.existsUserById(userId));
        assertTrue(chatService.existsById(chatId));

        usersByChatId = userService.getAllByChatId(chatId);
        int usersByChatIdListSizeAfterRegistration = usersByChatId.size();

        assertFalse(userService.getAllByChatId(chatId).isEmpty());
        assertEquals(usersByChatIdListSizeBeforeRegistration + 1, usersByChatIdListSizeAfterRegistration);

        chatsByUserId = chatService.getAllByUsersId(userId);
        int chatsByUserIdListSizeAfterRegistration = chatsByUserId.size();

        assertFalse(chatService.getAllByUsersId(userId).isEmpty());
        assertEquals(chatsByUserIdListSizeBeforeRegistration + 1, chatsByUserIdListSizeAfterRegistration);
    }

    @Test
    public void handleMessageWithExistingUserAndExistingChatShouldReturnSuccessfulRegistrationMessage() {
        Long chatId = 1L;
        Long userId = 1L;
        Update update = TelegramTestHelper.createMessageUpdateFromGroupNameWithChatIdAndUserId(chatId, userId);

        assertTrue(userService.existsUserById(userId));
        assertTrue(chatService.existsById(chatId));
        assertFalse(userService.getAllByChatId(chatId).isEmpty());
        assertFalse(chatService.getAllByUsersId(userId).isEmpty());

        SendMessage actualSendMessage = registrationHandler.handleMessage(update);

        assertEquals(chatId, Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(USER_ALREADY_REGISTERED, actualSendMessage.getText());

        assertTrue(userService.existsUserById(userId));
        assertTrue(chatService.existsById(chatId));
        assertFalse(userService.getAllByChatId(chatId).isEmpty());
        assertFalse(chatService.getAllByUsersId(userId).isEmpty());
    }
}
