package ua.zxc.cowbot.handler.types.command.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.BotApiSpringBootTest;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.RespectDTO;
import ua.zxc.cowbot.handler.types.command.RespectsHandler;
import ua.zxc.cowbot.helper.TelegramTestHelper;
import ua.zxc.cowbot.mapper.impl.RespectMapper;
import ua.zxc.cowbot.service.RespectService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_NUMBER_PAGE;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_PAGE_SIZE_FOR_RESPECTS;
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
public class RespectsHandlerIntTest extends BotApiSpringBootTest {

    @Autowired
    private RespectsHandler respectsHandler;

    @Autowired
    private RespectService respectService;

    @Autowired
    private RespectMapper respectMapper;

    @Test
    public void handlerMessageWithNotEmptyRespectListShouldReturnMessageWithList() {
        Long chatId = 1L;
        Update update = TelegramTestHelper.createMessageUpdateFromGroupNameWithChatId(chatId);

        SendMessage actualSendMessage = respectsHandler.handleMessage(update);

        PageBO<RespectDTO> allByChatId = respectService.getAllByChatId(chatId, DEFAULT_NUMBER_PAGE,
                DEFAULT_PAGE_SIZE_FOR_RESPECTS);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(respectMapper.dtosToString(allByChatId.getContent()), actualSendMessage.getText()
        );

    }

    @Test
    public void handlerMessageWithEmptyRespectListShouldReturnMessageWithList() {
        Update update = TelegramTestHelper.createMessageUpdateFromGroupNameWithChatId(100L);

        SendMessage actualSendMessage = respectsHandler.handleMessage(update);

        assertEquals(update.getMessage().getChatId(), Long.valueOf(actualSendMessage.getChatId()));
        assertEquals(HTML, actualSendMessage.getParseMode());
        assertEquals(NOTHING_TO_SHOW, actualSendMessage.getText());

    }
}
