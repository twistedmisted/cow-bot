package ua.zxc.cowbot.handler.types.callback;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.dto.PlaceDTO;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.helper.PlaceInitializr;
import ua.zxc.cowbot.helper.QueueInitializr;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.service.PlaceService;
import ua.zxc.cowbot.service.QueueService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.NOTHING_TO_SHOW;

public class ShowQueueListHandlerTest extends BotApiSpringBootTest {

    private static final Update UPDATE =
            TelegramTestHelper.createCallbackUpdateFromGroupChatWithMessageText("show_queue_places_list_by_id_1");

    @Autowired
    private ShowQueueListHandler showQueueListHandler;

    @MockBean
    private PlaceService placeService;

    @MockBean
    private QueueService queueService;

    @Autowired
    private PlaceMapper placeMapper;

    @Test
    public void handleMessageWithExistingPlacesForQueueShouldReturnPlacesMessage() {
        PlaceDTO placeDTO = PlaceInitializr.createDTO();
        QueueDTO queueDTO = QueueInitializr.createDTO();
        List<PlaceDTO> placeDTOList = Collections.singletonList(placeDTO);

        when(placeService.getAllByQueueId(anyLong()))
                .thenReturn(placeDTOList);
        when(queueService.getQueueById(anyLong()))
                .thenReturn(queueDTO);

        SendMessage actualSendMessage = showQueueListHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals("Список до черги '" +
                        queueDTO.getName() +
                        "'\n" +
                        placeMapper.dtosToString(placeDTOList),
                actualSendMessage.getText());
    }

    @Test
    public void handleMessageWithNotExistingPlacesForQueueShouldReturnPlacesMessage() {
        when(placeService.getAllByQueueId(anyLong()))
                .thenReturn(new ArrayList<>());

        SendMessage actualSendMessage = showQueueListHandler.handleMessage(UPDATE);

        assertEquals(UPDATE.getCallbackQuery().getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(NOTHING_TO_SHOW, actualSendMessage.getText());
    }
}