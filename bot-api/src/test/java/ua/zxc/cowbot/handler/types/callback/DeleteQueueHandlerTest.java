package ua.zxc.cowbot.handler.types.callback;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.helper.QueueInitializr;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.QueueService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.QUEUE_WITH_NAME_REMOVED;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

public class DeleteQueueHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE =
            TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText("delete_queue_by_id_1");

    @Autowired
    private DeleteQueueHandler deleteQueueHandler;

    @MockBean
    private QueueService queueService;

    @Test
    public void handleMessageWithExistingLessonIdShouldReturnLessonWithNameRemoved() {
        QueueDTO queueToRemove = QueueInitializr.createDTO();

        when(queueService.deleteQueue(eq(1L))).thenReturn(queueToRemove);

        SendMessage actualSendMessage = deleteQueueHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(String.format(QUEUE_WITH_NAME_REMOVED, queueToRemove.getName()), actualSendMessage.getText());
    }

    @Test
    public void handlerMessageWithNotExistingLessonIdShouldReturnTryAgainMessage() {
        SendMessage actualSendMessage = deleteQueueHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(String.format(TRY_AGAIN_WHAT_SOMETHING, "видалити чергу"), actualSendMessage.getText());
    }
}