package ua.zxc.cowbot.handler.types.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.service.UserService;

import java.util.List;

import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.OPERATION_SUCCESSFUL;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;
import static ua.zxc.cowbot.utils.TelegramUtils.mentionUser;

@Handler(value = "ALL", commands = "/all")
@Slf4j
@RequiredArgsConstructor
public class AllHandler implements HandlerStrategy {

    private static final int NUMBER_USERS_ON_PAGE = 5;

    private final TelegramService telegramService;
    private final UserService userService;

    @Override
    public SendMessage handleMessage(Update update) {
        telegramService.deleteMessage(update.getMessage());
        Long chatId = update.getMessage().getChatId();
        try {
            mentionAll(update);
        } catch (RuntimeException e) {
            log.warn("Cannot mention all users in chat with id: {}", chatId, e);
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "тегнути всіх користувачів"))
                    .parseMode(HTML)
                    .build();
        }
        // TODO: remove it
        return SendMessage.builder()
                .chatId(chatId)
                .text(OPERATION_SUCCESSFUL)
                .parseMode(HTML)
                .build();
    }

    private void mentionAll(Update update) {
        long chatId = update.getMessage().getChatId();
        int pageNum = 0;
        while (true) {
            List<UserDTO> allByChatId = userService.getAllByChatId(chatId, pageNum, NUMBER_USERS_ON_PAGE);
            if (allByChatId.isEmpty()) {
                break;
            }
            pageNum++;
            String messageTextToMentionUsers = createMessageTextToMentionUsers(allByChatId);
            sendMessageToMentionUsers(chatId, messageTextToMentionUsers);
        }
    }

    private String createMessageTextToMentionUsers(List<UserDTO> allByChatId) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allByChatId.size(); i++) {
            if (i + 1 == allByChatId.size()) {
                sb.append(mentionUser(allByChatId.get(i)));
                break;
            }
            sb.append(mentionUser(allByChatId.get(i))).append(", ");
        }
        return sb.toString();
    }

    private void sendMessageToMentionUsers(long chatId, String messageTextToMentionUsers) {
        telegramService.sendMessage(SendMessage.builder()
                .chatId(chatId)
                .text(messageTextToMentionUsers)
                .parseMode(HTML)
                .build());
    }
}
