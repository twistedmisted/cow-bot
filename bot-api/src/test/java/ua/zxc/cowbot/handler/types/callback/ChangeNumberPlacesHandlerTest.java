package ua.zxc.cowbot.handler.types.callback;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.QueueTaskDTO;
import ua.zxc.cowbot.helper.QueueTaskInitializr;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.QueueTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ua.zxc.cowbot.utils.Constants.CHANGE_PLACE_NUMBER_INFO_MESSAGE;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

public class ChangeNumberPlacesHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE = TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText("change_number_places_1");

    @Autowired
    private ChangeNumberPlacesHandler changeNumberPlacesHandler;

    @MockBean
    private QueueTaskService queueTaskService;

    @Test
    public void handleMessageWithNotExistingQueueTaskShouldReturnSuccessMessage() {
        when(queueTaskService.insertQueueTask(any(QueueTaskDTO.class))).thenReturn(QueueTaskInitializr.createDTO());

        SendMessage actualSendMessage = changeNumberPlacesHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(CHANGE_PLACE_NUMBER_INFO_MESSAGE, actualSendMessage.getText());
    }

    @Test
    public void handleMessageWithExistingQueueTaskShouldReturnUnsuccessfulMessage() {
        SendMessage actualSendMessage = changeNumberPlacesHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(String.format(TRY_AGAIN_WHAT_SOMETHING, "створити запит на зміну кількості місць у черзі"),
                actualSendMessage.getText());
    }
}