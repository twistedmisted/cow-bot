package ua.zxc.cowbot.handler.types.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.PlaceDTO;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.redis.entity.UserHashEntity;
import ua.zxc.cowbot.service.PlaceService;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.service.impl.UserCacheService;
import ua.zxc.cowbot.utils.ParseData;
import ua.zxc.cowbot.utils.TelegramUtils;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static ua.zxc.cowbot.Keyboard.createKeyboardForChangePlace;
import static ua.zxc.cowbot.Keyboard.createKeyboardForQueue;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.TelegramUtils.mentionUser;

@Handler(value = "TAKE_PLACE", commands = "take_place_")
@Slf4j
@RequiredArgsConstructor
public class TakePlaceHandler implements HandlerStrategy {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final UserCacheService userCacheService;

    private final PlaceService placeService;

    private final QueueService queueService;

    private final TelegramService telegramService;

    @Override
    public SendMessage handleMessage(Update update) {
        try {
            if (userCacheService.get("USER", update.getCallbackQuery().getFrom().getId().toString()) == null) {
                processTakingPlace(update);
            }
        } catch (RuntimeException e) {
            log.warn("Cannot take place, something went wrong", e);
            telegramService.answerCallbackQuery(AnswerCallbackQuery.builder()
                    .callbackQueryId(update.getCallbackQuery().getId())
                    .text(String.format(Constants.TRY_AGAIN_WHAT_SOMETHING, "знайти місце"))
                    .build()
            );
        }
        return new SendMessage();
    }

    private void processTakingPlace(Update update) {
        executor.execute(new TakePlaceRunnable(update.getCallbackQuery()));
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Cannot to await executor", e);
        }

    }

    private class TakePlaceRunnable implements Runnable {

        private final CallbackQuery callbackQuery;

        public TakePlaceRunnable(CallbackQuery callbackQuery) {
            this.callbackQuery = callbackQuery;
        }

        @Override
        public void run() {
            takePlace(callbackQuery);
        }
    }

    private void takePlace(CallbackQuery callbackQuery) {
        if (queueService.existsById(ParseData.parseLongAfterFirstUnderscore(callbackQuery.getData()))) {
            PlaceDTO place = createPlaceDTOFromCallbackQuery(callbackQuery);
            if (isPlaceFree(place)) {
                takeFreePlace(callbackQuery, place);
            } else {
                takeNotFreePlace(callbackQuery, place);
            }
        }
        telegramService.sendMessage(SendMessage.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .text(TelegramUtils.mentionUser(callbackQuery.getFrom()) + ", місце не зайнято, бо черги не існує.")
                .parseMode(HTML)
                .build());
    }

    private PlaceDTO createPlaceDTOFromCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        return PlaceDTO.builder()
                .userId(callbackQuery.getFrom().getId())
                .queueId(ParseData.parseLongAfterFirstUnderscore(data))
                .number(ParseData.parseIntAfterLastUnderscore(data))
                .build();
    }

    private boolean isPlaceFree(PlaceDTO place) {
        return placeService.isPlaceFree(place.getQueueId(), place.getNumber());
    }

    private void takeFreePlace(CallbackQuery callbackQuery, PlaceDTO place) {
        if (!existsByUserIdAndQueueId(place)) {
            takeFreePlaceByUserWithoutPlaceAndSendNotification(callbackQuery, place);
            return;
        }
        takeFreePlaceByUserWithPlaceAndSendNotification(callbackQuery, place);
    }

    private boolean existsByUserIdAndQueueId(PlaceDTO place) {
        return placeService.existsById(place.getUserId(), place.getQueueId());
    }

    private void takeFreePlaceByUserWithoutPlaceAndSendNotification(CallbackQuery callbackQuery, PlaceDTO placeToSave) {
        PlaceDTO savedPlace = savePlace(placeToSave);
        if (isNull(savedPlace)) {
            throw new RuntimeException("Cannot take place for user wit id: " + placeToSave.getUserId() +
                    " and queue id: " + placeToSave.getQueueId() + " and place number: " + placeToSave.getNumber());
        }
        userCacheService.save("USER", String.valueOf(placeToSave.getUserId()), getUpdateEntity(callbackQuery));
        editKeyboardForQueue(callbackQuery, queueService.getQueueById(placeToSave.getQueueId()));
        sendNotificationMessageForTookPlace(callbackQuery, savedPlace);
    }

    private PlaceDTO savePlace(PlaceDTO placeToSave) {
        return placeService.insertPlace(placeToSave);
    }

    private void editKeyboardForQueue(CallbackQuery callbackQuery, QueueDTO queue) {
        telegramService.editMessageReplyMarkup(EditMessageReplyMarkup.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .inlineMessageId(callbackQuery.getInlineMessageId())
                .replyMarkup(createKeyboardForQueue(queue))
                .build()
        );
    }

    private void sendNotificationMessageForTookPlace(CallbackQuery callbackQuery, PlaceDTO savedPlace) {
        telegramService.sendMessage(SendMessage.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .text(createTextForSuccessfulTookPlace(savedPlace))
                .parseMode(HTML)
                .build());
    }

    private String createTextForSuccessfulTookPlace(PlaceDTO savedPlace) {
        return mentionUser(savedPlace.getUser()) +
                ", зайняв(-ла) місце " +
                savedPlace.getNumber() +
                ".";
    }

    private void takeFreePlaceByUserWithPlaceAndSendNotification(CallbackQuery callbackQuery, PlaceDTO placeToUpdate) {
        int prevNumber = getPrevNumber(placeToUpdate);
        PlaceDTO updatedPlace = placeService.updatePlace(placeToUpdate);
        if (isNull(updatedPlace)) {
            throw new RuntimeException("Cannot change place for user wit id: " + placeToUpdate.getUserId() +
                    " and queue id: " + placeToUpdate.getQueueId() +
                    " and updated place number: " + placeToUpdate.getNumber() +
                    " and previous place number: " + prevNumber);
        }
        placeToUpdate = updatedPlace;
        userCacheService.save("USER", String.valueOf(placeToUpdate.getUserId()), getUpdateEntity(callbackQuery));
        editKeyboardForQueue(callbackQuery, queueService.getQueueById(placeToUpdate.getQueueId()));
        sendNotificationMessageForChangedPlace(callbackQuery, updatedPlace, prevNumber);
    }

    private int getPrevNumber(PlaceDTO placeToUpdate) {
        PlaceDTO userPlaceBeforeUpdate = placeService.getPlaceById(placeToUpdate.getUserId(), placeToUpdate.getQueueId());
        if (userPlaceBeforeUpdate == null) {
            throw new RuntimeException("The user does not have place in queue");
        }
        return userPlaceBeforeUpdate.getNumber();
    }

    private void sendNotificationMessageForChangedPlace(CallbackQuery callbackQuery, PlaceDTO updatedPlace, int prevNumber) {
        telegramService.sendMessage(SendMessage.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .text(createTextForSuccessfulChangedPlace(updatedPlace, prevNumber))
                .parseMode(HTML)
                .build());
    }

    private String createTextForSuccessfulChangedPlace(PlaceDTO savedPlace, int prevNumber) {
        return mentionUser(savedPlace.getUser()) +
                ", змінив(-ла) місце з " +
                prevNumber +
                " на " +
                savedPlace.getNumber() +
                ".";
    }

    private void takeNotFreePlace(CallbackQuery callbackQuery, PlaceDTO placeToUpdate) {
        PlaceDTO placeFrom = placeService.getPlaceById(placeToUpdate.getUserId(), placeToUpdate.getQueueId());
        if (placeFrom == null) {
            showNotificationThatPlaceIsNotFree(callbackQuery, placeToUpdate);
            return;
        }
        createAndSendProposeMessageToSwapPlaces(callbackQuery, placeToUpdate, placeFrom);
    }

    private void showNotificationThatPlaceIsNotFree(CallbackQuery callbackQuery, PlaceDTO placeToUpdate) {
        telegramService.answerCallbackQuery(AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text("Місце під номером " + placeToUpdate.getNumber() + " зайняте, оберіть інше.")
                .build()
        );
    }

    private void createAndSendProposeMessageToSwapPlaces(CallbackQuery callbackQuery, PlaceDTO placeToUpdate,
                                                         PlaceDTO placeFrom) {
        PlaceDTO placeTo = placeService.getPlaceByQueueIdAndNumber(placeToUpdate.getQueueId(), placeToUpdate.getNumber());
        UserDTO userFrom = placeFrom.getUser();
        UserDTO userTo = placeTo.getUser();
        if (!userTo.equals(placeFrom.getUser())) {
            telegramService.sendMessage(SendMessage.builder()
                    .chatId(callbackQuery.getMessage().getChatId())
                    .text(createTextForSwapUserPlaces(userFrom, userTo))
                    .parseMode(HTML)
                    .replyMarkup(createKeyboardForChangePlace(placeFrom, placeTo, placeTo.getQueueId()))
                    .build());
        }
    }

    private String createTextForSwapUserPlaces(UserDTO userFrom, UserDTO userTo) {
        return mentionUser(userTo) +
                ", " +
                userFrom.getFirstName() +
                " хоче помінятись з тобою місцем.";
    }

    private UserHashEntity getUpdateEntity(CallbackQuery callbackQuery) {
        UserHashEntity updateEntity = new UserHashEntity();
        updateEntity.setUserId(callbackQuery.getFrom().getId());
        updateEntity.setUserName(callbackQuery.getFrom().getUserName());
        updateEntity.setLocalDateTime(LocalDateTime.now());
        updateEntity.setChatId(callbackQuery.getMessage().getChatId());
        return updateEntity;
    }
}
