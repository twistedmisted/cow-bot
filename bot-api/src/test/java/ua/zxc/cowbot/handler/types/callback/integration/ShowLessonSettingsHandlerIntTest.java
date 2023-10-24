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
import ua.zxc.cowbot.handler.types.callback.ShowLessonSettingsHandler;
import ua.zxc.cowbot.service.TelegramService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ua.zxc.cowbot.helper.TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.LESSON_NOT_EXISTS;
import static ua.zxc.cowbot.utils.Constants.LESSON_SETTINGS_TITLE;

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
public class ShowLessonSettingsHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private ShowLessonSettingsHandler showLessonSettingsHandler;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageText> captor;

    @Test
    public void handleMessageWithExistingLessonIdShouldReturnSettingsMessage() {
        Long chatId = 1L;
        long lessonId = 1;
        Update update =
                createCallbackUpdateFromGroupChatWithMessageText(chatId, "show_lesson_settings_by_id_" + lessonId);

        showLessonSettingsHandler.handleMessage(update);

        verify(telegramService, times(1)).editMessageText(captor.capture());

        EditMessageText editMessage = captor.getValue();
        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(editMessage.getChatId()));
        assertEquals(HTML, editMessage.getParseMode());
        assertEquals(LESSON_SETTINGS_TITLE, editMessage.getText());
        assertNotNull(editMessage.getReplyMarkup());
    }

    @Test
    public void handleMessageWithNotExistingLessonIdShouldReturnSettingsMessage() {
        Long chatId = 1L;
        long lessonId = 1000;
        Update update =
                createCallbackUpdateFromGroupChatWithMessageText(chatId, "show_lesson_settings_by_id_" + lessonId);

        showLessonSettingsHandler.handleMessage(update);

        verify(telegramService, times(1)).editMessageText(captor.capture());

        EditMessageText editMessage = captor.getValue();
        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(editMessage.getChatId()));
        assertEquals(HTML, editMessage.getParseMode());
        assertEquals(LESSON_NOT_EXISTS, editMessage.getText());
        assertNull(editMessage.getReplyMarkup());
    }
}