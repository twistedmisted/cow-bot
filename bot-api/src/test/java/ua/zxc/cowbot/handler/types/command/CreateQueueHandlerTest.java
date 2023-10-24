package ua.zxc.cowbot.handler.types.command;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.exception.ObjectAlreadyExistsException;
import ua.zxc.cowbot.helper.QueueInitializr;
import ua.zxc.cowbot.service.QueueService;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ua.zxc.cowbot.helper.TelegramTestHelper.createMessageUpdateFromGroupChatWithMessageText;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.QUEUE_ALREADY_EXISTS_WITH_NAME;
import static ua.zxc.cowbot.utils.Constants.QUEUE_CREATED;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

public class CreateQueueHandlerTest extends BotApiSpringBootTest {

    private static final QueueDTO QUEUE_DTO = QueueInitializr.createDTO();

    @Autowired
    private CreateQueueHandler createQueueHandler;

    @MockBean
    private QueueService queueService;

    @Test
    public void handleMessageWithEmptyNameForQueueShouldCreateQueueWithDefaultName() {
        Update update = createMessageUpdateFromGroupChatWithMessageText("/create");

        when(queueService.insertQueue(any(QueueDTO.class))).thenAnswer(i -> {
            Object[] args = i.getArguments();
            return args[0];
        });

        SendMessage actualSendMessage = createQueueHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertTrue(actualSendMessage.getText().contains("Черга "));
    }

    @Test
    public void handleMessageWithNameForQueueShouldCreateQueueWithEnteredName() {
        Update update = createMessageUpdateFromGroupChatWithMessageText("/create TEST QUEUE");

        when(queueService.insertQueue(any(QueueDTO.class))).thenReturn(QUEUE_DTO);

        SendMessage actualSendMessage = createQueueHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(format(QUEUE_CREATED, QUEUE_DTO.getName()), actualSendMessage.getText());
    }

    @Test
    public void handleMessageWithRuntimeExceptionShouldSendMessageQueueNotCreated() {
        Update update = createMessageUpdateFromGroupChatWithMessageText("/create SOME NAME");

        when(queueService.insertQueue(any(QueueDTO.class))).thenReturn(null);

        SendMessage actualSendMessage = createQueueHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(format(TRY_AGAIN_WHAT_SOMETHING, "створити чергу"), actualSendMessage.getText());
    }

    @Test
    public void handleMessageWithExistingQueueNameShouldSendMessageQueueAlreadyExists() {
        String queueName = "EXISTING QUEUE NAME";
        Update update = createMessageUpdateFromGroupChatWithMessageText("/create " + queueName);

        when(queueService.insertQueue(any(QueueDTO.class))).thenThrow(ObjectAlreadyExistsException.class);

        SendMessage actualSendMessage = createQueueHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(format(QUEUE_ALREADY_EXISTS_WITH_NAME, queueName), actualSendMessage.getText());
    }
}