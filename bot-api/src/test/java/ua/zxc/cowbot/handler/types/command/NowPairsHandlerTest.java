package ua.zxc.cowbot.handler.types.command;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.scheduleapi.exception.JSONObjectConvertException;
import ua.zxc.cowbot.scheduleapi.exception.LessonFinishedException;
import ua.zxc.cowbot.scheduleapi.exception.LessonNotStartedException;
import ua.zxc.cowbot.scheduleapi.exception.PairNotFoundException;
import ua.zxc.cowbot.scheduleapi.service.ScheduleService;
import ua.zxc.cowbot.service.ChatService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static ua.zxc.cowbot.helper.ChatInitializr.createDTO;
import static ua.zxc.cowbot.helper.ChatInitializr.createDTOWithoutGroupName;
import static ua.zxc.cowbot.utils.Constants.ENTER_GROUP_NAME;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.CHAT_NEED_REGISTRATION;
import static ua.zxc.cowbot.utils.Constants.NOT_PAIRS_TODAY;
import static ua.zxc.cowbot.utils.Constants.NOT_PAIR_NOW;
import static ua.zxc.cowbot.utils.Constants.PAIRS_FINISHED;
import static ua.zxc.cowbot.utils.Constants.PAIR_NOT_STARTED;

public class NowPairsHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE = TelegramTestHelper.createMessageUpdateFromGroupChat();

    @Autowired
    private NowPairsHandler nowPairsHandler;

    @MockBean
    private ScheduleService scheduleService;

    @MockBean
    private ChatService chatService;

    @Test
    public void handlerMessageWithExistingGroupNameAndExistingNowPairsShouldReturnMessageWithPairs()
            throws LessonNotStartedException, PairNotFoundException, LessonFinishedException, JSONObjectConvertException {
        String nowPairs = "Pairs now";

        when(scheduleService.getNowPairs(anyString())).thenReturn(nowPairs);
        when(chatService.getChatById(anyLong())).thenReturn(createDTO());

        SendMessage actualSendMessage = nowPairsHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(nowPairs, actualSendMessage.getText());
    }

    @Test
    public void handlerMessageWithNotExistingChatShouldReturnNeedRegistrationMessage() {
        when(chatService.getChatById(anyLong())).thenReturn(null);

        SendMessage actualSendMessage = nowPairsHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(CHAT_NEED_REGISTRATION, actualSendMessage.getText());
    }

    @Test
    public void handlerMessageWithNotExistingGroupNameShouldReturnEnterGroupNameMessage() {
        when(chatService.getChatById(anyLong())).thenReturn(createDTOWithoutGroupName());

        SendMessage actualSendMessage = nowPairsHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(ENTER_GROUP_NAME, actualSendMessage.getText());
    }

    @Test
    public void handlerMessageWithExistingGroupNameAndNotStartedNowPairsShouldReturnPairNotStartMessage()
            throws LessonNotStartedException, PairNotFoundException, LessonFinishedException, JSONObjectConvertException {
        when(scheduleService.getNowPairs(anyString())).thenThrow(LessonNotStartedException.class);
        when(chatService.getChatById(anyLong())).thenReturn(createDTO());

        SendMessage actualSendMessage = nowPairsHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(PAIR_NOT_STARTED, actualSendMessage.getText());
    }

    @Test
    public void handlerMessageWithExistingGroupNameAndPairFinishedShouldReturnPairFinishedMessage()
            throws LessonNotStartedException, PairNotFoundException, LessonFinishedException, JSONObjectConvertException {
        when(scheduleService.getNowPairs(anyString())).thenThrow(LessonFinishedException.class);
        when(chatService.getChatById(anyLong())).thenReturn(createDTO());

        SendMessage actualSendMessage = nowPairsHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(PAIRS_FINISHED, actualSendMessage.getText());
    }

    @Test
    public void handlerMessageWithExistingGroupNameAndNotNowPairShouldReturnNotPairNowMessage()
            throws LessonNotStartedException, PairNotFoundException, LessonFinishedException, JSONObjectConvertException {
        when(scheduleService.getNowPairs(anyString())).thenThrow(PairNotFoundException.class);
        when(chatService.getChatById(anyLong())).thenReturn(createDTO());

        SendMessage actualSendMessage = nowPairsHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(NOT_PAIR_NOW, actualSendMessage.getText());
    }

    @Test
    public void handlerMessageWithExistingGroupNameAndNotPairsTodayShouldReturnNotPairsTodayMessage()
            throws LessonNotStartedException, PairNotFoundException, LessonFinishedException, JSONObjectConvertException {
        when(scheduleService.getNowPairs(anyString())).thenThrow(JSONObjectConvertException.class);
        when(chatService.getChatById(anyLong())).thenReturn(createDTO());

        SendMessage actualSendMessage = nowPairsHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(NOT_PAIRS_TODAY, actualSendMessage.getText());
    }
}