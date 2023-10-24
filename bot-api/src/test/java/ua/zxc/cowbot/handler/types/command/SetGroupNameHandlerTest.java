package ua.zxc.cowbot.handler.types.command;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.helper.ChatInitializr;
import ua.zxc.cowbot.service.ChatService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ua.zxc.cowbot.helper.TelegramTestHelper.createMessageUpdateFromGroupChatWithMessageText;
import static ua.zxc.cowbot.utils.Constants.GROUP_NAME_UPDATED;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.CHAT_NEED_REGISTRATION;
import static ua.zxc.cowbot.utils.Constants.SPECIFY_GROUP_NAME;

public class SetGroupNameHandlerTest extends BotApiSpringBootTest {

    private static final String GROUP_NAME = "XX-00";
    private static final Update UPDATE_WITH_GROUP_NAME = createMessageUpdateFromGroupChatWithMessageText("/g " + GROUP_NAME);
    private static final Update UPDATE_WITHOUT_GROUP_NAME = createMessageUpdateFromGroupChatWithMessageText("/g");

    @Autowired
    private SetGroupNameHandler setGroupNameHandler;

    @MockBean
    private ChatService chatService;

    @Test
    public void handleMessageWithExistingChatShouldReturnGroupNameUpdatedMessage() {
        when(chatService.updateChat(any(ChatDTO.class))).thenReturn(ChatInitializr.createDTO());

        SendMessage actualSendMessage = setGroupNameHandler.handleMessage(UPDATE_WITH_GROUP_NAME);

        assertEquals(UPDATE_WITH_GROUP_NAME.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(String.format(GROUP_NAME_UPDATED, GROUP_NAME), actualSendMessage.getText());
    }

    @Test
    public void handleMessageWithNotExistingChatShouldReturnNeedRegistrationMessage() {
        when(chatService.updateChat(any(ChatDTO.class))).thenReturn(null);

        SendMessage actualSendMessage = setGroupNameHandler.handleMessage(UPDATE_WITH_GROUP_NAME);

        assertEquals(UPDATE_WITH_GROUP_NAME.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(CHAT_NEED_REGISTRATION, actualSendMessage.getText());
    }

    @Test
    public void handleMessageWithoutGroupNameShouldReturnSpecifyGroupNameMessage() {
        SendMessage actualSendMessage = setGroupNameHandler.handleMessage(UPDATE_WITHOUT_GROUP_NAME);

        assertEquals(UPDATE_WITHOUT_GROUP_NAME.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(SPECIFY_GROUP_NAME, actualSendMessage.getText());
    }
}