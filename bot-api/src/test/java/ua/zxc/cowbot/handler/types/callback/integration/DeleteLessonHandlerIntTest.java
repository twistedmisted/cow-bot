package ua.zxc.cowbot.handler.types.callback.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.handler.types.callback.DeleteLessonHandler;
import ua.zxc.cowbot.service.LessonService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.zxc.cowbot.helper.TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.LESSON_WITH_NAME_REMOVED;
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
public class DeleteLessonHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private DeleteLessonHandler deleteLessonHandler;

    @Autowired
    private LessonService lessonService;

    @Test
    public void handleMessageWithExistingLessonIdShouldReturnLessonWithNameRemoved() {
        Long chatId = 1L;
        Long lessonId = 1L;
        Update update = createCallbackUpdateFromGroupChatWithMessageText(chatId, "delete_lesson_by_id_" + lessonId);
        LessonDTO lessonToRemove = lessonService.getLessonById(lessonId);

        assertTrue(lessonService.existsById(lessonId));

        SendMessage actualSendMessage = deleteLessonHandler.handleMessage(update);

        assertFalse(lessonService.existsById(lessonId));

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(String.format(LESSON_WITH_NAME_REMOVED, lessonToRemove.getName()), actualSendMessage.getText());
    }

    @Test
    public void handlerMessageWithNotExistingLessonIdShouldReturnTryAgainMessage() {
        Long chatId = 1L;
        Long lessonId = 100L;
        Update update = createCallbackUpdateFromGroupChatWithMessageText(chatId, "delete_lesson_by_id_" + lessonId);

        assertFalse(lessonService.existsById(lessonId));

        SendMessage actualSendMessage = deleteLessonHandler.handleMessage(update);

        assertFalse(lessonService.existsById(lessonId));

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(String.format(TRY_AGAIN_WHAT_SOMETHING, "видалити предмет"), actualSendMessage.getText());
    }
}