package ua.zxc.cowbot.handler.types.callback;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.helper.LessonInitializr;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.LessonService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.LESSON_WITH_NAME_REMOVED;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

public class DeleteLessonHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE =
            TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText("delete_lesson_by_id_1");

    @Autowired
    private DeleteLessonHandler deleteLessonHandler;

    @MockBean
    private LessonService lessonService;

    @Test
    public void handleMessageWithExistingLessonIdShouldReturnLessonWithNameRemoved() {
        LessonDTO lessonToRemove = LessonInitializr.createDTO();

        when(lessonService.deleteLesson(eq(1L))).thenReturn(lessonToRemove);

        SendMessage actualSendMessage = deleteLessonHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(String.format(LESSON_WITH_NAME_REMOVED, lessonToRemove.getName()), actualSendMessage.getText());
    }

    @Test
    public void handlerMessageWithNotExistingLessonIdShouldReturnTryAgainMessage() {
        SendMessage actualSendMessage = deleteLessonHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(String.format(TRY_AGAIN_WHAT_SOMETHING, "видалити предмет"), actualSendMessage.getText());
    }
}