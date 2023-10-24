package ua.zxc.cowbot.handler.types.callback;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.QUEUE_NOT_EXISTS;
import static ua.zxc.cowbot.utils.Constants.QUEUE_SETTINGS_TITLE;

class ShowQueueSettingsHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE =
            TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText("show_lesson_settings_by_id_1");

    @Autowired
    private ShowQueueSettingsHandler showQueueSettingsHandler;

    @MockBean
    private QueueService queueService;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageText> captor;

    @Test
    public void handleMessageWithExistingLessonIdShouldReturnSettingsMessage() {
        when(queueService.existsById(anyLong())).thenReturn(true);
        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        showQueueSettingsHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).editMessageText(captor.capture());

        EditMessageText editMessage = captor.getValue();
        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(editMessage.getChatId()));
        assertEquals(HTML, editMessage.getParseMode());
        assertEquals(QUEUE_SETTINGS_TITLE, editMessage.getText());
        assertNotNull(editMessage.getReplyMarkup());
    }

    @Test
    public void handleMessageWithNotExistingLessonIdShouldReturnSettingsMessage() {
        when(queueService.existsById(anyLong())).thenReturn(false);
        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        showQueueSettingsHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).editMessageText(captor.capture());

        EditMessageText editMessage = captor.getValue();
        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(editMessage.getChatId()));
        assertEquals(HTML, editMessage.getParseMode());
        assertEquals(QUEUE_NOT_EXISTS, editMessage.getText());
        assertNull(editMessage.getReplyMarkup());
    }
}