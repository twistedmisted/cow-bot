package ua.zxc.cowbot.handler.types.callback.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.PlaceDTO;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.handler.types.callback.ShowQueueListHandler;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.PlaceService;
import ua.zxc.cowbot.service.QueueService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.NOTHING_TO_SHOW;

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
public class ShowQueueListHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private ShowQueueListHandler showQueueListHandler;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private PlaceMapper placeMapper;

    @Test
    public void handleMessageWithExistingPlacesForQueueShouldReturnPlacesMessage() {
        long chatId = 1L;
        Long queueId = 1L;
        Update update = TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText(chatId, "show_queue_places_list_by_id_" + queueId);

        SendMessage actualSendMessage = showQueueListHandler.handleMessage(update);

        QueueDTO queueById = queueService.getQueueById(queueId);
        List<PlaceDTO> allByQueueId = placeService.getAllByQueueId(queueId);

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals("Список до черги '" +
                        queueById.getName() +
                        "'\n" +
                        placeMapper.dtosToString(allByQueueId),
                actualSendMessage.getText());
    }

    @Test
    public void handleMessageWithNotExistingPlacesForQueueShouldReturnPlacesMessage() {
        long chatId = 1L;
        long queueId = 1000L;
        Update update = TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText(chatId, "show_queue_places_list_by_id_" + queueId);

        SendMessage actualSendMessage = showQueueListHandler.handleMessage(update);

        assertEquals(update.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(NOTHING_TO_SHOW, actualSendMessage.getText());
    }
}