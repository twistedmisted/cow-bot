package ua.zxc.cowbot.handler.types.callback.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.PlaceDTO;
import ua.zxc.cowbot.handler.types.callback.TakePlaceHandler;
import ua.zxc.cowbot.service.PlaceService;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.service.impl.UserCacheService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static ua.zxc.cowbot.helper.TelegramTestHelper.createCallbackUpdateFromGroupChat;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.TelegramUtils.mentionUser;

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
public class TakePlaceHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private TakePlaceHandler takePlaceHandler;

    @Autowired
    private QueueService queueService;

    @Autowired
    private PlaceService placeService;

    @MockBean
    private UserCacheService userCacheService;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageText> editMessageTextCaptor;

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;

    @BeforeEach
    public void setUp() {
        when(userCacheService.get(anyString(), anyString())).thenReturn(null);
    }

    @Test
    public void handleMessageWithExistingQueueAndFreePlaceAndNotExistingUserInQueueShouldTakePlace() {
        Long userId = 7L;
        Long queueId = 1L;
        Integer placeNumber = 7;
        long chatId = 1;
        Update update = createCallbackUpdateFromGroupChat(chatId, userId, queueId + "_take_place_" + placeNumber);

        assertTrue(placeService.isPlaceFree(queueId, placeNumber));

        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));
        doNothing().when(telegramService).sendMessage(any(SendMessage.class));

        takePlaceHandler.handleMessage(update);

        verify(telegramService, times(1)).sendMessage(sendMessageCaptor.capture());

        SendMessage actualSendMessage = sendMessageCaptor.getValue();

        PlaceDTO placeById = placeService.getPlaceById(userId, queueId);

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(mentionUser(placeById.getUser()) +
                        ", зайняв(-ла) місце " +
                        placeById.getNumber() +
                        ".",
                actualSendMessage.getText());

        assertFalse(placeService.isPlaceFree(queueId, placeNumber));
    }
}