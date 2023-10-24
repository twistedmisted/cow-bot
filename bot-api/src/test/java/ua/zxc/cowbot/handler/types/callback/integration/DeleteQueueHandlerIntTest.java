package ua.zxc.cowbot.handler.types.callback.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.handler.types.callback.DeleteQueueHandler;
import ua.zxc.cowbot.service.QueueService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.zxc.cowbot.helper.TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.QUEUE_WITH_NAME_REMOVED;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

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
@Transactional
public class DeleteQueueHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private DeleteQueueHandler deleteQueueHandler;

    @Autowired
    private QueueService queueService;

    @Test
    public void handleMessageWithExistingLessonIdShouldReturnLessonWithNameRemoved() {
        Long chatId = 1L;
        Long queueId = 1L;
        Update update = createCallbackUpdateFromGroupChatWithMessageText(chatId, "delete_queue_by_id_" + queueId);
        QueueDTO queueToRemove = queueService.getQueueById(queueId);

        assertTrue(queueService.existsById(queueId));

        SendMessage actualSendMessage = deleteQueueHandler.handleMessage(update);

        assertFalse(queueService.existsById(queueId));

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(String.format(QUEUE_WITH_NAME_REMOVED, queueToRemove.getName()), actualSendMessage.getText());
    }

    @Test
    public void handlerMessageWithNotExistingLessonIdShouldReturnTryAgainMessage() {
        Long chatId = 1L;
        Long queueId = 100L;
        Update update = createCallbackUpdateFromGroupChatWithMessageText(chatId, "delete_queue_by_id_" + queueId);

        assertFalse(queueService.existsById(queueId));

        SendMessage actualSendMessage = deleteQueueHandler.handleMessage(update);

        assertFalse(queueService.existsById(queueId));

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(String.format(TRY_AGAIN_WHAT_SOMETHING, "видалити чергу"), actualSendMessage.getText());
    }
}