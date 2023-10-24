package ua.zxc.cowbot.handler.types.callback;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.PlaceDTO;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.PlaceService;
import ua.zxc.cowbot.service.QueueService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.utils.ParseData;

import java.util.List;

import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.NOTHING_TO_SHOW;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

/**
 * Showing a places for queue
 */
@Handler(value = "SHOW_QUEUE_PLACES_LIST_BY_ID", commands = "show_queue_places_list_by_id_")
@Slf4j
public class ShowQueueListHandler implements HandlerStrategy {

    private final QueueService queueService;

    private final PlaceService placeService;

    private final PlaceMapper placeMapper;

    private final TelegramService telegramService;

    public ShowQueueListHandler(QueueService queueService, PlaceService placeService, PlaceMapper placeMapper,
                                TelegramService telegramService) {
        this.queueService = queueService;
        this.placeService = placeService;
        this.placeMapper = placeMapper;
        this.telegramService = telegramService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try {
            return printQueuePlacesList(callbackQuery);
        } catch (RuntimeException e) {
            log.warn("Cannot print queue places list, something went wrong", e);
            telegramService.answerCallbackQuery(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "не вдалось вивести список для черги"))
                    .build()
            );
        }
        return new SendMessage();
    }

    private SendMessage printQueuePlacesList(CallbackQuery callbackQuery) {
        Long queueId = ParseData.parseLongAfterLastUnderscore(callbackQuery.getData());
        List<PlaceDTO> places = placeService.getAllByQueueId(queueId);
        return SendMessage.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .text(createMessageTextForQueuePlacesList(places))
                .parseMode(HTML)
                .build();
    }

    private String createMessageTextForQueuePlacesList(List<PlaceDTO> places) {
        if (places.isEmpty()) {
            return NOTHING_TO_SHOW;
        }
        QueueDTO queueById = queueService.getQueueById(places.get(0).getQueueId());
        return "Список до черги '" +
                queueById.getName() +
                "'\n" +
                placeMapper.dtosToString(places);
    }
}
