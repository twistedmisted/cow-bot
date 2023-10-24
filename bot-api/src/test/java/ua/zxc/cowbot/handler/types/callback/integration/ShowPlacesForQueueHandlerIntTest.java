package ua.zxc.cowbot.handler.types.callback.integration;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.handler.types.callback.ShowPlacesForQueueHandler;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ua.zxc.cowbot.helper.TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.QUEUE_REMOVED;

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
class ShowPlacesForQueueHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private ShowPlacesForQueueHandler showPlacesForQueueHandler;

    @Autowired
    private QueueService queueService;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageText> editMessageTextCaptor;

    @Test
    public void handleUpdateWithExistingQueueIdShouldReturnPlacesForQueue() {
        Long queueId = 1L;
        Update update = createCallbackUpdateFromGroupChatWithMessageText("show_places_for_queue_by_id_" + queueId);

        QueueDTO queueDTO = queueService.getQueueById(queueId);

        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        showPlacesForQueueHandler.handleMessage(update);

        verify(telegramService, times(1)).editMessageText(editMessageTextCaptor.capture());

        EditMessageText actualEditMessageText = editMessageTextCaptor.getValue();

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualEditMessageText.getChatId()));
        assertEquals(HTML, actualEditMessageText.getParseMode());
        assertEquals("<b>" + queueDTO.getName() + "</b>", actualEditMessageText.getText());
        assertEquals(Math.round(queueDTO.getSize() / 5.0) + 1,
                actualEditMessageText.getReplyMarkup().getKeyboard().size());
    }

    @Test
    public void handleUpdateWithNotExistingQueueIdShouldReturnQueueRemovedMessage() {
        long queueId = 100L;
        Update update = createCallbackUpdateFromGroupChatWithMessageText("show_places_for_queue_by_id_" + queueId);

        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        showPlacesForQueueHandler.handleMessage(update);

        verify(telegramService, times(1)).editMessageText(editMessageTextCaptor.capture());

        EditMessageText actualEditMessageText = editMessageTextCaptor.getValue();

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualEditMessageText.getChatId()));
        assertEquals(HTML, actualEditMessageText.getParseMode());
        assertEquals(QUEUE_REMOVED, actualEditMessageText.getText());
    }
}