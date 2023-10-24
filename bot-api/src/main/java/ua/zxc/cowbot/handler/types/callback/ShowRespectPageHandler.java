package ua.zxc.cowbot.handler.types.callback;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.RespectDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.mapper.impl.RespectMapper;
import ua.zxc.cowbot.service.RespectService;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.utils.ParseData;

import static ua.zxc.cowbot.Keyboard.createKeyboardForRespectsList;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_PAGE_SIZE_FOR_RESPECTS;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

@Handler(value = "SHOW_RESPECT_PAGE", commands = "show_respect_page_")
@Slf4j
public class ShowRespectPageHandler implements HandlerStrategy {

    private final RespectService respectService;

    private final RespectMapper respectMapper;

    private final TelegramService telegramService;

    public ShowRespectPageHandler(RespectService respectService, RespectMapper respectMapper, TelegramService telegramService) {
        this.respectService = respectService;
        this.respectMapper = respectMapper;
        this.telegramService = telegramService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try {
            printRespects(callbackQuery);
        } catch (RuntimeException e) {
            log.warn("Cannot show respect page, something went wrong", e);
            telegramService.answerCallbackQuery(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "відобразити сторінку з вподобайками"))
                    .build()
            );
        }
        return new SendMessage();
    }

    private void printRespects(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        int pageNumber = ParseData.parseIntAfterLastUnderscore(callbackQuery.getData());
        PageBO<RespectDTO> respectPage = respectService.getAllByChatId(chatId, pageNumber, DEFAULT_PAGE_SIZE_FOR_RESPECTS);
        if (respectPage.isEmpty()) {
            throw new RuntimeException("Cannot open respect page: [pageNumber = '" + pageNumber + "']");
        }
        editMessageToPrintRespects(callbackQuery, respectPage);
    }

    private void editMessageToPrintRespects(CallbackQuery callbackQuery, PageBO<RespectDTO> respectPage) {
        telegramService.editMessageText(EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(createMessageText(respectPage))
                .parseMode(HTML)
                .replyMarkup(createKeyboardForRespectsList(respectPage))
                .build()
        );
    }

    private String createMessageText(PageBO<RespectDTO> respectPage) {
        return respectMapper.dtosToString(respectPage.getContent());
    }
}
