package ua.zxc.cowbot.handler.types.callback.integration;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.handler.types.callback.ShowLessonHandler;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.LessonService;
import ua.zxc.cowbot.service.TelegramService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ua.zxc.cowbot.utils.Constants.CANNOT_SHOW_LESSON_BY_ID;
import static ua.zxc.cowbot.utils.Constants.HTML;

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
class ShowLessonHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private ShowLessonHandler showLessonHandler;

    @Autowired
    private LessonService lessonService;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageText> captorEditMessageText;

    @Captor
    private ArgumentCaptor<AnswerCallbackQuery> captorAnswerCallbackQuery;

    @Test
    public void handleMessageWithExistingLessonByIdShouldReturnLessonMessage() {
        Long lessonId = 1L;
        Update update = TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText("show_lesson_by_id_" +
                lessonId);

        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        showLessonHandler.handleMessage(update);

        verify(telegramService, times(1)).editMessageText(captorEditMessageText.capture());

        StringBuilder expectedMessageText = getExpectedMessageText(lessonService.getLessonById(lessonId));
        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(captorEditMessageText.getValue().getChatId()));
        assertEquals(HTML, captorEditMessageText.getValue().getParseMode());
        assertEquals(expectedMessageText.toString(), captorEditMessageText.getValue().getText());
    }

    @Test
    public void handleMessageWithNotExistingLessonByIdShouldReturnLessonMessage() {
        Long lessonId = 1000L;
        Update update = TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText("show_lesson_by_id_" +
                lessonId);

        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        showLessonHandler.handleMessage(update);

        verify(telegramService, times(1)).answerCallbackQuery(captorAnswerCallbackQuery.capture());

        assertEquals(update.getCallbackQuery().getId(), captorAnswerCallbackQuery.getValue().getCallbackQueryId());
        assertEquals(CANNOT_SHOW_LESSON_BY_ID, captorAnswerCallbackQuery.getValue().getText());
    }

    private static StringBuilder getExpectedMessageText(LessonDTO lesson) {
        StringBuilder expectedMessageText = new StringBuilder();
        expectedMessageText.append("<b>Предмет:</b><i> ")
                .append(lesson.getName()).append("</i>")
                .append(System.lineSeparator())
                .append("<b>Викладач:</b><i> ")
                .append(lesson.getFullTeacherName()).append("</i>")
                .append(System.lineSeparator());
        if (lesson.getEmail() != null && !lesson.getEmail().isEmpty()) {
            expectedMessageText.append("<b>Пошта:</b><i> ")
                    .append(lesson.getEmail()).append("</i>")
                    .append(System.lineSeparator());
        }
        if (lesson.getPhone() != null && !lesson.getPhone().isEmpty()) {
            expectedMessageText.append("<b>Телефон:</b><i> ")
                    .append(lesson.getPhone()).append("</i>")
                    .append(System.lineSeparator());
        }
        return expectedMessageText;
    }
}