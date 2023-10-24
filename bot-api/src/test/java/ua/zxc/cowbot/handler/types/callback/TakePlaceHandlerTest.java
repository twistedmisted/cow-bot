package ua.zxc.cowbot.handler.types.callback;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.PlaceDTO;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.helper.PlaceInitializr;
import ua.zxc.cowbot.helper.QueueInitializr;
import ua.zxc.cowbot.service.PlaceService;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.service.impl.UserCacheService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ua.zxc.cowbot.helper.TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.TelegramUtils.mentionUser;

public class TakePlaceHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE = createCallbackUpdateFromGroupChatWithMessageText("1_take_place_1");

    @Autowired
    private TakePlaceHandler takePlaceHandler;

    @MockBean
    private UserCacheService userCacheService;

    @MockBean
    private PlaceService placeService;

    @MockBean
    private QueueService queueService;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;

    @Captor
    private ArgumentCaptor<AnswerCallbackQuery> answerCallbackQueryCaptor;

    @BeforeEach
    public void setUp() {
        when(userCacheService.get(anyString(), anyString())).thenReturn(null);
    }

    @Test
    public void handleMessageWithExistingQueueAndFreePlaceAndNotExistingUserInQueueShouldTakePlace() {
        PlaceDTO placeDTO = PlaceInitializr.createDTO();
        QueueDTO queueDTO = QueueInitializr.createDTOWithPlaces();

        when(placeService.isPlaceFree(anyLong(), anyInt())).thenReturn(true);
        when(placeService.existsById(anyLong(), anyLong())).thenReturn(false);
        when(placeService.insertPlace(any(PlaceDTO.class))).thenReturn(placeDTO);
        when(queueService.getQueueById(anyLong())).thenReturn(queueDTO);

        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));
        doNothing().when(telegramService).sendMessage(any(SendMessage.class));

        takePlaceHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).sendMessage(sendMessageCaptor.capture());

        SendMessage actualSendMessage = sendMessageCaptor.getValue();

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(mentionUser(placeDTO.getUser()) +
                        ", зайняв(-ла) місце " +
                        placeDTO.getNumber() +
                        ".",
                actualSendMessage.getText());
    }

    @Test
    public void handleMessageWithExistingQueueAndFreePlaceAndExistingUserInQueueShouldTakePlace() {
        PlaceDTO placeDTO = PlaceInitializr.createDTO();
        QueueDTO queueDTO = QueueInitializr.createDTOWithPlaces();

        when(placeService.isPlaceFree(anyLong(), anyInt())).thenReturn(true);
        when(placeService.existsById(anyLong(), anyLong())).thenReturn(true);
        when(placeService.updatePlace(any(PlaceDTO.class))).thenReturn(placeDTO);
        when(placeService.getPlaceById(anyLong(), anyLong())).thenReturn(placeDTO);
        when(queueService.getQueueById(anyLong())).thenReturn(queueDTO);

        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));
        doNothing().when(telegramService).sendMessage(any(SendMessage.class));

        takePlaceHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).sendMessage(sendMessageCaptor.capture());

        SendMessage actualSendMessage = sendMessageCaptor.getValue();

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(mentionUser(placeDTO.getUser()) +
                        ", змінив(-ла) місце з " +
                        placeDTO.getNumber() +
                        " на " +
                        placeDTO.getNumber() +
                        ".",
                actualSendMessage.getText());
    }

    @Test
    public void handleMessageWithExistingQueueAndNotFreePlaceAndExistingUserInQueueShouldTakePlace() {
        PlaceDTO firstPlaceDTO = PlaceInitializr.createDTO();
        PlaceDTO secondPlaceDTO = PlaceInitializr.createDTO(10L, 10);
        QueueDTO queueDTO = QueueInitializr.createDTOWithPlaces();

        when(placeService.isPlaceFree(anyLong(), anyInt())).thenReturn(false);
        when(placeService.existsById(anyLong(), anyLong())).thenReturn(true);
        when(placeService.updatePlace(any(PlaceDTO.class))).thenReturn(firstPlaceDTO);
        when(placeService.getPlaceById(anyLong(), anyLong())).thenReturn(secondPlaceDTO);
        when(placeService.getPlaceByQueueIdAndNumber(anyLong(), anyInt())).thenReturn(firstPlaceDTO);
        when(queueService.getQueueById(anyLong())).thenReturn(queueDTO);

        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));
        doNothing().when(telegramService).sendMessage(any(SendMessage.class));

        takePlaceHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).sendMessage(sendMessageCaptor.capture());

        SendMessage actualSendMessage = sendMessageCaptor.getValue();

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(mentionUser(firstPlaceDTO.getUser()) +
                        ", " +
                        secondPlaceDTO.getUser().getFirstName() +
                        " хоче помінятись з тобою місцем.",
                actualSendMessage.getText());
    }

    @Test
    public void handleMessageWithExistingQueueAndNotFreePlaceAndNotExistingUserInQueueShouldTakePlace() {
        PlaceDTO placeDTO = PlaceInitializr.createDTO();
        placeDTO.setNumber(1);
        QueueDTO queueDTO = QueueInitializr.createDTOWithPlaces();

        when(placeService.isPlaceFree(anyLong(), anyInt())).thenReturn(false);
        when(placeService.existsById(anyLong(), anyLong())).thenReturn(true);
        when(placeService.getPlaceByQueueIdAndNumber(anyLong(), anyInt())).thenReturn(placeDTO);
        when(queueService.getQueueById(anyLong())).thenReturn(queueDTO);

        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));
        doNothing().when(telegramService).answerCallbackQuery(any(AnswerCallbackQuery.class));
        doNothing().when(telegramService).sendMessage(any(SendMessage.class));

        takePlaceHandler.handleMessage(UPDATE);

        verify(telegramService, times(1)).answerCallbackQuery(answerCallbackQueryCaptor.capture());

        AnswerCallbackQuery answerCallbackQuery = answerCallbackQueryCaptor.getValue();

        assertEquals("Місце під номером " + placeDTO.getNumber() + " зайняте, оберіть інше.",
                answerCallbackQuery.getText());
    }
}