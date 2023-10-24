package ua.zxc.cowbot.handler.types.command;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.helper.TelegramTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ua.zxc.cowbot.utils.Constants.HELP;
import static ua.zxc.cowbot.utils.Constants.HTML;

class HelpHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE = TelegramTestHelper.createMessageUpdateFromGroupChat();

    @Autowired
    private HelpHandler helpHandler;

    @Test
    public void handleMessageWithUpdateShouldReturnMessage() {
        SendMessage actualSendMessage = helpHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HELP, actualSendMessage.getText());
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertNotNull(actualSendMessage.getReplyMarkup());
    }
}