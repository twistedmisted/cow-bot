package ua.zxc.cowbot.handler.types.callback.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.handler.types.callback.ChangeNumberPlacesHandler;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.QueueTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ua.zxc.cowbot.utils.Constants.CHANGE_PLACE_NUMBER_INFO_MESSAGE;
import static ua.zxc.cowbot.utils.Constants.HTML;

public class ChangeNumberPlacesHandlerIntTest extends BotApiSpringBootTest {

    private static final Update UPDATE = TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText("change_number_places_1");

    @Autowired
    private ChangeNumberPlacesHandler changeNumberPlacesHandler;

    @Autowired
    private QueueTaskService queueTaskService;

    @Test
    public void handleMessageWithNotExistingQueueTaskShouldReturnSuccessMessage() {
        CallbackQuery callbackQuery = UPDATE.getCallbackQuery();

        assertNull(queueTaskService.getQueueTaskByUserIdAndChatId(callbackQuery.getFrom().getId(), callbackQuery.getMessage().getChatId()));

        SendMessage actualSendMessage = changeNumberPlacesHandler.handleMessage(UPDATE);

        assertEquals(callbackQuery.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(CHANGE_PLACE_NUMBER_INFO_MESSAGE, actualSendMessage.getText());

        assertNotNull(queueTaskService.getQueueTaskByUserIdAndChatId(callbackQuery.getFrom().getId(), callbackQuery.getMessage().getChatId()));
    }
}
