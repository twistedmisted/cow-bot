package ua.zxc.cowbot.handler.types.callback;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.helper.QueueInitializr;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.PlaceService;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ua.zxc.cowbot.utils.Constants.*;
import static ua.zxc.cowbot.utils.TelegramUtils.mentionUser;

class LeaveQueueHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE =
            TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText("leave_queue_by_id_1");

    @Autowired
    private LeaveQueueHandler leaveQueueHandler;

    @MockBean
    private QueueService queueService;

    @MockBean
    private PlaceService placeService;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageReplyMarkup> markupArgumentCaptor;

    @Captor
    private ArgumentCaptor<AnswerCallbackQuery> answerCallbackQueryArgumentCaptor;

    @Test
    public void handleMessageWithExistingPlaceShouldReturnLeaveQueueMessage() {
        QueueDTO queue = QueueInitializr.createDTOWithPlaces();

        when(placeService.existsById(anyLong(), anyLong())).thenReturn(true);
        when(placeService.deletePlace(anyLong(), anyLong())).thenReturn(true);
        when(queueService.getQueueById(anyLong())).thenReturn(queue);
        doNothing().when(telegramService).editMessageReplyMarkup(any());

        SendMessage actualSendMessage = leaveQueueHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).editMessageReplyMarkup(markupArgumentCaptor.capture());

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(mentionUser(UPDATE.getCallbackQuery().getFrom()) + LEAVE_QUEUE, actualSendMessage.getText());

        InlineKeyboardMarkup actualReplyMarkup = markupArgumentCaptor.getValue().getReplyMarkup();

        assertEquals((queue.getSize() / 5) + 1, actualReplyMarkup.getKeyboard().size());
    }

    @Test
    public void handleMessageWithNotExistingPlaceShouldReturnNotLeaveQueueMessage() {
        when(placeService.existsById(anyLong(), anyLong())).thenReturn(false);
        doNothing().when(telegramService).editMessageReplyMarkup(any());

        leaveQueueHandler.handleMessage(UPDATE);

        verify(placeService, times(0)).deletePlace(anyLong(), anyLong());
        verify(telegramService, times(0)).editMessageReplyMarkup(any(EditMessageReplyMarkup.class));
        verify(telegramService, times(1)).answerCallbackQuery(answerCallbackQueryArgumentCaptor.capture());

        assertEquals(UPDATE.getCallbackQuery().getId(), answerCallbackQueryArgumentCaptor.getValue().getCallbackQueryId());
        assertEquals(USER_NOT_IN_QUEUE, answerCallbackQueryArgumentCaptor.getValue().getText());
    }
}