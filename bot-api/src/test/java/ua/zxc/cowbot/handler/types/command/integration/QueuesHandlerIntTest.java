package ua.zxc.cowbot.handler.types.command.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.handler.types.command.QueuesHandler;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.QueueService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_NUMBER_PAGE;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_PAGE_SIZE;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.NOTHING_TO_SHOW;
import static ua.zxc.cowbot.utils.Constants.QUEUE_LIST_TITLE;

@SpringJUnitConfig
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
public class QueuesHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private QueuesHandler queuesHandler;

    @Autowired
    private QueueService queueService;

    @Test
    public void handleMessageWithExistingQueuesByChatIdShouldReturnMessageWithQueues() {
        Long chatId = 1L;
        Update update = TelegramTestHelper.createMessageUpdateFromGroupChatWithMessageText(chatId);

        SendMessage actualSendMessage = queuesHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(QUEUE_LIST_TITLE, actualSendMessage.getText());
        assertNotNull(actualSendMessage.getReplyMarkup());

        InlineKeyboardMarkup actualInlineKeyboardMarkup = (InlineKeyboardMarkup) actualSendMessage.getReplyMarkup();
        List<List<InlineKeyboardButton>> actualKeyboard = actualInlineKeyboardMarkup.getKeyboard();

        PageBO<QueueDTO> queuePage = queueService.getAllByChatId(chatId, DEFAULT_NUMBER_PAGE, DEFAULT_PAGE_SIZE);
        List<QueueDTO> queuesByChatId = queuePage.getContent();
        QueueDTO queueDTO = queuesByChatId.get(0);

        assertEquals(7, actualKeyboard.size());

        InlineKeyboardButton firstQueueButton = actualKeyboard.get(0).get(0);

        assertEquals(queueDTO.getName(), firstQueueButton.getText());
        assertEquals("show_places_for_queue_by_id_" + queueDTO.getId(), firstQueueButton.getCallbackData());

        List<InlineKeyboardButton> paginationButtons = actualKeyboard.get(5);

        assertEquals("1/2", paginationButtons.get(0).getText());
        assertEquals("show_queue_page_1", paginationButtons.get(1).getCallbackData());
    }

    @Test
    public void handleMessageWithNoExistingLessonsByChatIdShouldReturnMessageWithLessons() {
        Update update = TelegramTestHelper.createMessageUpdateFromGroupNameWithChatId(999L);

        SendMessage actualSendMessage = queuesHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(NOTHING_TO_SHOW, actualSendMessage.getText());
        assertNull(actualSendMessage.getReplyMarkup());
    }
}
