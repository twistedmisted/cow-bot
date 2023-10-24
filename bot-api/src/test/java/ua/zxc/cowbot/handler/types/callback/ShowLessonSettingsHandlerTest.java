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
import ua.zxc.cowbot.service.LessonService;
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
import static ua.zxc.cowbot.utils.Constants.LESSON_NOT_EXISTS;
import static ua.zxc.cowbot.utils.Constants.LESSON_SETTINGS_TITLE;

public class ShowLessonSettingsHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE =
            TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText("show_lesson_settings_by_id_1");

    @Autowired
    private ShowLessonSettingsHandler showLessonSettingsHandler;

    @MockBean
    private LessonService lessonService;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageText> captor;

    @Test
    public void handleMessageWithExistingLessonIdShouldReturnSettingsMessage() {
        when(lessonService.existsById(anyLong())).thenReturn(true);
        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        showLessonSettingsHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).editMessageText(captor.capture());

        EditMessageText editMessage = captor.getValue();
        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(editMessage.getChatId()));
        assertEquals(HTML, editMessage.getParseMode());
        assertEquals(LESSON_SETTINGS_TITLE, editMessage.getText());
        assertNotNull(editMessage.getReplyMarkup());
    }

    @Test
    public void handleMessageWithNotExistingLessonIdShouldReturnSettingsMessage() {
        when(lessonService.existsById(anyLong())).thenReturn(false);
        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        showLessonSettingsHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).editMessageText(captor.capture());

        EditMessageText editMessage = captor.getValue();
        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(editMessage.getChatId()));
        assertEquals(HTML, editMessage.getParseMode());
        assertEquals(LESSON_NOT_EXISTS, editMessage.getText());
        assertNull(editMessage.getReplyMarkup());
    }
}