package ua.zxc.cowbot.handler.types.callback;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.PlaceDTO;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.PlaceService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.service.UserService;
import ua.zxc.cowbot.utils.Emoji;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static ua.zxc.cowbot.utils.Constants.*;

@Handler(value = "CONFIRM_PLACE_CHANGING", commands = "confirm_place_changing_")
@Slf4j
public class ConfirmPlaceChangingHandler implements HandlerStrategy {

    private final PlaceService placeService;

    private final UserService userService;

    private final TelegramService telegramService;

    public ConfirmPlaceChangingHandler(PlaceService placeService, UserService userService,
                                       TelegramService telegramService) {
        this.placeService = placeService;
        this.userService = userService;
        this.telegramService = telegramService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try {
            processSwappingUsers(callbackQuery);
        } catch (RuntimeException e) {
            log.warn("Cannot swap users, something went wrong", e);
            telegramService.answerCallbackQuery(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "поміняти вас місцями"))
                    .build());
        }
        return new SendMessage();
    }

    private void processSwappingUsers(CallbackQuery callbackQuery) {
        String text = swapUsersAndReturnMessageText(callbackQuery);
        if (text.isBlank()) {
            return;
        }
        telegramService.editMessageText(EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(text)
                .parseMode(HTML)
                .build()
        );
    }

    private String swapUsersAndReturnMessageText(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long userIdFrom = parseUserIdFrom(data);
        Long userIdTo = parseUserIdTo(data);
        Long queueId = parseQueueId(data);
        PlaceDTO userPlaceTo = placeService.getPlaceById(userIdTo, queueId);
        if (isNull(userPlaceTo)) {
            return createMessageTextToNotifyUserLeftQueue(userIdTo);
        }
        if (isNotUserToSwapping(callbackQuery, userPlaceTo)) {
            showNotificationForUsersThatDoNotTakePartInSwapping(callbackQuery);
            return "";
        }
        PlaceDTO userPlaceFrom = placeService.getPlaceById(userIdFrom, queueId);
        if (isNull(userPlaceFrom)) {
            return createMessageTextToNotifyUserLeftQueue(userIdFrom);
        }
        swapUsers(userPlaceTo, userPlaceFrom);
        return userPlaceFrom.getUser().getFirstName()
                + ' '
                + Emoji.REPEAT
                + ' '
                + userPlaceTo.getUser().getFirstName();
    }

    private Long parseUserIdFrom(String data) {
        StringBuilder sb = new StringBuilder(data);
        int indexOfStartUserId = sb.indexOf("pf_") + 3;
        int indexOfEndUserId = sb.indexOf("_", indexOfStartUserId);
        return Long.parseLong(sb.substring(indexOfStartUserId, indexOfEndUserId));
    }

    private Long parseUserIdTo(String data) {
        StringBuilder sb = new StringBuilder(data);
        int indexOfStartUserId = sb.indexOf("pt_") + 3;
        return Long.parseLong(sb.substring(indexOfStartUserId));
    }

    private Long parseQueueId(String data) {
        StringBuilder sb = new StringBuilder(data);
        int indexOfStartQueueId = sb.indexOf("q_") + 2;
        int indexOfEndQueueId = sb.indexOf("_", indexOfStartQueueId);
        return Long.parseLong(sb.substring(indexOfStartQueueId, indexOfEndQueueId));
    }

    private String createMessageTextToNotifyUserLeftQueue(Long userId) {
        UserDTO user = userService.getUserById(userId);
        if (isNull(user)) {
            throw new RuntimeException("User with id " + userId + " does not exists");
        }
        return user.getFirstName() + LEAVE_QUEUE_CANNOT_SWAP;
    }

    private static boolean isNotUserToSwapping(CallbackQuery callbackQuery, PlaceDTO userPlaceTo) {
        return !callbackQuery.getFrom().getId().equals(userPlaceTo.getUser().getId());
    }

    private void showNotificationForUsersThatDoNotTakePartInSwapping(CallbackQuery callbackQuery) {
        telegramService.answerCallbackQuery(AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text("Вас забанено. Дякуємо за співпрацю (ШУТКА, ХАХАХАХАХАХАХ)")
                .showAlert(true)
                .build()
        );
    }

    private void swapUsers(PlaceDTO userPlaceTo, PlaceDTO userPlaceFrom) {
        int newPlace = userPlaceTo.getNumber();
        int oldPlace = userPlaceFrom.getNumber();
        userPlaceFrom.setNumber(newPlace);
        userPlaceTo.setNumber(oldPlace);
        updateSwappedPlaces(userPlaceTo, userPlaceFrom);
    }

    private void updateSwappedPlaces(PlaceDTO userPlaceTo, PlaceDTO userPlaceFrom) {
        List<PlaceDTO> placeEntities = new ArrayList<>(2);
        placeEntities.add(userPlaceFrom);
        placeEntities.add(userPlaceTo);
        placeService.updatePlaces(placeEntities);
    }
}
