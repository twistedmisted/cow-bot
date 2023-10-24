package ua.zxc.cowbot.handler.types.command;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.helper.LessonInitializr;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.LessonService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.LESSON_LIST_TITLE;
import static ua.zxc.cowbot.utils.Constants.NOTHING_TO_SHOW;

public class LessonsHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE = TelegramTestHelper.createMessageUpdateFromGroupChat();

    @Autowired
    private LessonsHandler lessonsHandler;

    @MockBean
    private LessonService lessonService;

    @Test
    public void handleMessageWithExistingLessonsByChatIdShouldReturnMessageWithLessons() {
        LessonDTO lessonDTO = LessonInitializr.createDTO();
        when(lessonService.getAllByChatId(anyLong(), anyInt(), anyInt()))
                .thenReturn(PageBO.<LessonDTO>builder()
                        .totalPages(2)
                        .currentPageNumber(0)
                        .content(Collections.nCopies(5, lessonDTO))
                        .build());

        SendMessage actualSendMessage = lessonsHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(LESSON_LIST_TITLE, actualSendMessage.getText());
        assertNotNull(actualSendMessage.getReplyMarkup());

        InlineKeyboardMarkup actualInlineKeyboardMarkup = (InlineKeyboardMarkup) actualSendMessage.getReplyMarkup();
        List<List<InlineKeyboardButton>> actualKeyboard = actualInlineKeyboardMarkup.getKeyboard();

        assertEquals(7, actualKeyboard.size());

        InlineKeyboardButton firstLessonButton = actualKeyboard.get(0).get(0);

        assertEquals(lessonDTO.getName(), firstLessonButton.getText());
        assertEquals("show_lesson_by_id_" + lessonDTO.getId(), firstLessonButton.getCallbackData());

        List<InlineKeyboardButton> paginationButtons = actualKeyboard.get(5);

        assertEquals("1/2", paginationButtons.get(0).getText());
        assertEquals("show_lesson_page_1", paginationButtons.get(1).getCallbackData());
    }

    @Test
    public void handleMessageWithNoExistingLessonsByChatIdShouldReturnMessageWithLessons() {
        when(lessonService.getAllByChatId(anyLong(), anyInt(), anyInt()))
                .thenReturn(new PageBO<>());

        SendMessage actualSendMessage = lessonsHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(NOTHING_TO_SHOW, actualSendMessage.getText());
        assertNull(actualSendMessage.getReplyMarkup());
    }
}