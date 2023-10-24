package ua.zxc.cowbot.handler.types.callback;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.PlaceDTO;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.helper.PlaceInitializr;
import ua.zxc.cowbot.helper.UserInitializr;
import ua.zxc.cowbot.service.PlaceService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.service.UserService;
import ua.zxc.cowbot.utils.Emoji;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ua.zxc.cowbot.helper.TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.LEAVE_QUEUE_CANNOT_SWAP;

class ConfirmPlaceChangingHandlerTest extends BotApiSpringBootTest {

    @Autowired
    private ConfirmPlaceChangingHandler confirmPlaceChangingHandler;

    @MockBean
    private UserService userService;

    @MockBean
    private PlaceService placeService;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageText> editMessageTextCaptor;

    @Test
    public void handleMessageWithExistingUserAndPlaceShouldSwapUsers() {
        Long queueId = 1L;

        Long userIdTo = 1111L;
        Integer placeNumberTo = 10;
        PlaceDTO userPlaceTo = PlaceInitializr.createDTO(userIdTo, placeNumberTo);

        Integer placeNumberFrom = 10;
        Long userIdFrom = 1L;
        PlaceDTO userPlaceFrom = PlaceInitializr.createDTO(userIdFrom, placeNumberFrom);

        Update update = createCallbackUpdateFromGroupChatWithMessageText(userIdTo,
                String.format("confirm_place_changing_q_%d_pf_%d_pt_%d", queueId, userIdFrom, userIdTo));

        when(placeService.getPlaceById(eq(userIdTo), anyLong())).thenReturn(userPlaceTo);
        when(placeService.getPlaceById(eq(userIdFrom), anyLong())).thenReturn(userPlaceFrom);
        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        confirmPlaceChangingHandler.handleMessage(update);

        verify(telegramService, times(1)).editMessageText(editMessageTextCaptor.capture());

        EditMessageText actualEditMessageText = editMessageTextCaptor.getValue();

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualEditMessageText.getChatId()));
        assertEquals(HTML, actualEditMessageText.getParseMode());
        assertEquals(userPlaceFrom.getUser().getFirstName() +
                        ' ' +
                        Emoji.REPEAT +
                        ' ' +
                        userPlaceTo.getUser().getFirstName(),
                actualEditMessageText.getText());
    }

    @Test
    public void handleMessageWithNotExistingPlaceToShouldReturnNotifyMessage() {
        Update update = createCallbackUpdateFromGroupChatWithMessageText("confirm_place_changing_q_1_pf_1_pt_2");

        UserDTO userTo = UserInitializr.createUserDTO();

        when(userService.getUserById(anyLong())).thenReturn(userTo);
        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        confirmPlaceChangingHandler.handleMessage(update);

        verify(telegramService, times(1)).editMessageText(editMessageTextCaptor.capture());

        EditMessageText actualEditMessageText = editMessageTextCaptor.getValue();

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualEditMessageText.getChatId()));
        assertEquals(HTML, actualEditMessageText.getParseMode());
        assertEquals(userTo.getFirstName() + LEAVE_QUEUE_CANNOT_SWAP, actualEditMessageText.getText());

        verify(placeService, times(0)).getPlaceById(eq(1L), eq(1L));
    }

    @Test
    public void handleMessageWithNotExistingPlaceFromShouldReturnNotifyMessage() {
        Long queueId = 1L;

        Long userIdTo = 1111L;
        Integer placeNumberTo = 10;
        PlaceDTO userPlaceTo = PlaceInitializr.createDTO(userIdTo, placeNumberTo);

        Long userIdFrom = 1L;

        Update update = createCallbackUpdateFromGroupChatWithMessageText(userIdTo,
                String.format("confirm_place_changing_q_%d_pf_%d_pt_%d", queueId, userIdFrom, userIdTo));

        UserDTO userTo = UserInitializr.createUserDTO();

        when(userService.getUserById(anyLong())).thenReturn(userTo);
        when(placeService.getPlaceById(eq(userIdTo), anyLong())).thenReturn(userPlaceTo);
        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        confirmPlaceChangingHandler.handleMessage(update);

        verify(telegramService, times(1)).editMessageText(editMessageTextCaptor.capture());

        EditMessageText actualEditMessageText = editMessageTextCaptor.getValue();

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualEditMessageText.getChatId()));
        assertEquals(HTML, actualEditMessageText.getParseMode());
        assertEquals(userTo.getFirstName() + LEAVE_QUEUE_CANNOT_SWAP, actualEditMessageText.getText());

        verify(placeService, times(0)).updatePlaces(any());
    }
}