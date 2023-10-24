package ua.zxc.cowbot.handler.types.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.RespectDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.mapper.impl.RespectMapper;
import ua.zxc.cowbot.service.RespectService;
import ua.zxc.cowbot.service.TelegramService;

import static ua.zxc.cowbot.Keyboard.createKeyboardForRespectsList;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_NUMBER_PAGE;
import static ua.zxc.cowbot.utils.Constants.DEFAULT_PAGE_SIZE_FOR_RESPECTS;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.NOTHING_TO_SHOW;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

@Handler(value = "RESPECTS", commands = "/respects")
@Slf4j
@RequiredArgsConstructor
public class RespectsHandler implements HandlerStrategy {

    private final RespectService respectService;
    private final RespectMapper respectMapper;
    private final TelegramService telegramService;

    @Override
    public SendMessage handleMessage(Update update) {
        telegramService.deleteMessage(update.getMessage());
        try {
            return printRespects(update);
        } catch (RuntimeException e) {
            Long chatId = update.getMessage().getChatId();
            log.warn("Cannot print respects list users in chat with id: {}", chatId, e);
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "вивести рейтинг вподобайок"))
                    .parseMode(HTML)
                    .build();
        }
    }

    private SendMessage printRespects(Update update) {
        Long chatId = update.getMessage().getChatId();
        PageBO<RespectDTO> respectPage = respectService.getAllByChatId(chatId, DEFAULT_NUMBER_PAGE,
                DEFAULT_PAGE_SIZE_FOR_RESPECTS);
        return createMessageForRespects(respectPage, chatId);
    }

    private SendMessage createMessageForRespects(PageBO<RespectDTO> respectPage, Long chatId) {
        SendMessage messageToSend = SendMessage.builder()
                .chatId(chatId)
                .text(createMessageText(respectPage))
                .parseMode(HTML)
                .build();
        if (respectPage.getTotalPages() > 1) {
            messageToSend.setReplyMarkup(createKeyboardForRespectsList(respectPage));
        }
        return messageToSend;
    }

    private String createMessageText(PageBO<RespectDTO> respectPage) {
        if (respectPage.isEmpty()) {
            return NOTHING_TO_SHOW;
        }
        return respectMapper.dtosToString(respectPage.getContent());
    }
}
