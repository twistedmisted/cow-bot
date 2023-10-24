package ua.zxc.cowbot.handler.types.callback;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.helper.QueueInitializr;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ua.zxc.cowbot.helper.TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.QUEUE_REMOVED;

class ShowPlacesForQueueHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE = createCallbackUpdateFromGroupChatWithMessageText("show_places_for_queue_by_id_1");

    @Autowired
    private ShowPlacesForQueueHandler showPlacesForQueueHandler;

    @MockBean
    private QueueService queueService;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageText> editMessageTextCaptor;

    @Test
    public void handleUpdateWithExistingQueueIdShouldReturnPlacesForQueue() {
        QueueDTO queueDTO = QueueInitializr.createDTOWithPlaces();

        when(queueService.getQueueById(eq(1L))).thenReturn(queueDTO);
        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        showPlacesForQueueHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).editMessageText(editMessageTextCaptor.capture());

        EditMessageText actualEditMessageText = editMessageTextCaptor.getValue();

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualEditMessageText.getChatId()));
        assertEquals(HTML, actualEditMessageText.getParseMode());
        assertEquals("<b>" + queueDTO.getName() + "</b>", actualEditMessageText.getText());
        assertEquals(Math.round(queueDTO.getSize() / 5.0) + 1,
                actualEditMessageText.getReplyMarkup().getKeyboard().size());
    }

    @Test
    public void handleUpdateWithNotExistingQueueIdShouldReturnQueueRemovedMessage() {
        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        showPlacesForQueueHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).editMessageText(editMessageTextCaptor.capture());

        EditMessageText actualEditMessageText = editMessageTextCaptor.getValue();

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualEditMessageText.getChatId()));
        assertEquals(HTML, actualEditMessageText.getParseMode());
        assertEquals(QUEUE_REMOVED, actualEditMessageText.getText());
    }
}