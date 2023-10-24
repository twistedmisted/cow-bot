package ua.zxc.cowbot.handler.types.callback.integration;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.PlaceDTO;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.handler.types.callback.ConfirmPlaceChangingHandler;
import ua.zxc.cowbot.service.PlaceService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.service.UserService;
import ua.zxc.cowbot.utils.Emoji;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;
import static ua.zxc.cowbot.helper.TelegramTestHelper.createCallbackUpdateFromGroupChat;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.LEAVE_QUEUE_CANNOT_SWAP;

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
class ConfirmPlaceChangingHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private ConfirmPlaceChangingHandler confirmPlaceChangingHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    @MockBean
    private TelegramService telegramService;

    @Captor
    private ArgumentCaptor<EditMessageText> editMessageTextCaptor;

    @Test
    public void handleMessageWithExistingUserAndPlaceShouldSwapUsers() {
        Long queueId = 1L;

        Long userIdTo = 1L;
        PlaceDTO userPlaceTo = placeService.getPlaceById(userIdTo, queueId);

        Long userIdFrom = 2L;
        PlaceDTO userPlaceFrom = placeService.getPlaceById(userIdFrom, queueId);

        Integer userPlaceNumberTo = userPlaceTo.getNumber();
        Integer userPlaceNumberFrom = userPlaceFrom.getNumber();

        assertNotEquals(userPlaceNumberTo, userPlaceNumberFrom);

        Update update = createCallbackUpdateFromGroupChat(1L, userIdTo,
                String.format("confirm_place_changing_q_%d_pf_%d_pt_%d", queueId, userIdFrom, userIdTo));

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

        PlaceDTO newUserPlaceTo = placeService.getPlaceById(userIdTo, queueId);
        PlaceDTO newUserPlaceFrom = placeService.getPlaceById(userIdFrom, queueId);

        assertNotEquals(userPlaceNumberTo, newUserPlaceTo.getNumber());
        assertNotEquals(userPlaceNumberFrom, newUserPlaceFrom.getNumber());

        assertEquals(userPlaceNumberTo, newUserPlaceFrom.getNumber());
        assertEquals(userPlaceNumberFrom, newUserPlaceTo.getNumber());
    }

    @Test
    public void handleMessageWithNotExistingPlaceToShouldReturnNotifyMessage() {
        Long queueId = 1L;
        Long userIdTo = 7L;
        Long userIdFrom = 2L;
        Update update = createCallbackUpdateFromGroupChat(1L, userIdTo,
                String.format("confirm_place_changing_q_%d_pf_%d_pt_%d", queueId, userIdFrom, userIdTo));

        UserDTO userTo = userService.getUserById(userIdTo);

        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        confirmPlaceChangingHandler.handleMessage(update);

        verify(telegramService, times(1)).editMessageText(editMessageTextCaptor.capture());

        EditMessageText actualEditMessageText = editMessageTextCaptor.getValue();

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualEditMessageText.getChatId()));
        assertEquals(HTML, actualEditMessageText.getParseMode());
        assertEquals(userTo.getFirstName() + LEAVE_QUEUE_CANNOT_SWAP, actualEditMessageText.getText());
    }

    @Test
    public void handleMessageWithNotExistingPlaceFromShouldReturnNotifyMessage() {
        Long queueId = 1L;
        Long userIdTo = 2L;
        Long userIdFrom = 7L;
        Update update = createCallbackUpdateFromGroupChat(1L, userIdTo,
                String.format("confirm_place_changing_q_%d_pf_%d_pt_%d", queueId, userIdFrom, userIdTo));

        UserDTO userFrom = userService.getUserById(userIdFrom);

        doNothing().when(telegramService).editMessageText(any(EditMessageText.class));

        confirmPlaceChangingHandler.handleMessage(update);

        verify(telegramService, times(1)).editMessageText(editMessageTextCaptor.capture());

        EditMessageText actualEditMessageText = editMessageTextCaptor.getValue();

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualEditMessageText.getChatId()));
        assertEquals(HTML, actualEditMessageText.getParseMode());
        assertEquals(userFrom.getFirstName() + LEAVE_QUEUE_CANNOT_SWAP, actualEditMessageText.getText());
    }
}