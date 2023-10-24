package ua.zxc.cowbot.handler.types.command.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.handler.types.command.CreateQueueHandler;
import ua.zxc.cowbot.service.QueueService;

import java.util.List;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.zxc.cowbot.helper.TelegramTestHelper.createMessageUpdateFromGroupChatWithMessageText;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.CHAT_NEED_REGISTRATION;
import static ua.zxc.cowbot.utils.Constants.QUEUE_ALREADY_EXISTS_WITH_NAME;
import static ua.zxc.cowbot.utils.Constants.QUEUE_CREATED;

@Sql(
        value = {
                "classpath:/db-scripts/clear-tables.sql",
                "classpath:/db-scripts/populate-tables.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        value = {
                "classpath:/db-scripts/clear-tables.sql"
        },
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
public class CreateQueueHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private CreateQueueHandler createQueueHandler;

    @Autowired
    private QueueService queueService;

    @Test
    public void handlerMessageWithUpdateAndEmptyQueueNameShouldCreateQueueWithDefaultName() {
        Long chatId = 1L;
        Update update = createMessageUpdateFromGroupChatWithMessageText(chatId, "/create");

        SendMessage actualSendMessage = createQueueHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertTrue(actualSendMessage.getText().contains("Черга "));

        QueueDTO createdQueue = queueService.getQueueById(2L);

        assertNotNull(createdQueue);
        assertFalse(createdQueue.getName().isBlank());
        assertEquals(update.getMessage().getChatId(), createdQueue.getChatId());
    }

    @Test
    public void handlerMessageWithUpdateAndNameForQueueShouldCreateQueueWithEnteredName() {
        Long chatId = 1L;
        String queueName = "NOT EXISTING QUEUE NAME";
        Update update = createMessageUpdateFromGroupChatWithMessageText(chatId, "/create " + queueName);

        SendMessage actualSendMessage = createQueueHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(format(QUEUE_CREATED, queueName), actualSendMessage.getText());

        QueueDTO createdQueue = queueService.getQueueById(2L);

        assertNotNull(createdQueue);
        assertFalse(createdQueue.getName().isBlank());
        assertEquals(update.getMessage().getChatId(), createdQueue.getChatId());
    }

    @Test
    public void handleMessageWithExistingQueueNameShouldSendMessageQueueAlreadyExists() {
        Long chatId = 1L;
        String queueName = "First name";
        Update update = createMessageUpdateFromGroupChatWithMessageText(chatId, "/create " + queueName);

        List<QueueDTO> allByChatId = queueService.getAllByChatId(update.getMessage().getChatId());
        int oldSize = allByChatId.size();

        SendMessage actualSendMessage = createQueueHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(format(QUEUE_ALREADY_EXISTS_WITH_NAME, queueName), actualSendMessage.getText());

        allByChatId = queueService.getAllByChatId(update.getMessage().getChatId());
        int newSize = allByChatId.size();

        assertEquals(oldSize, newSize);
    }

    @Test
    public void handleMessageWithNotExistingChatShouldNeedRegistrationMessage() {
        Long chatId = 10000L;
        String queueName = "First queue";
        Update update = createMessageUpdateFromGroupChatWithMessageText(chatId, "/create " + queueName);

        List<QueueDTO> allByChatId = queueService.getAllByChatId(update.getMessage().getChatId());
        int oldSize = allByChatId.size();

        SendMessage actualSendMessage = createQueueHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(CHAT_NEED_REGISTRATION, actualSendMessage.getText());

        allByChatId = queueService.getAllByChatId(update.getMessage().getChatId());
        int newSize = allByChatId.size();

        assertEquals(oldSize, newSize);
    }
}
