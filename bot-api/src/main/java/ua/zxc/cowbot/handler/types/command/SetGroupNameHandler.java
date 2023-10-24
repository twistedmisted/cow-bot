package ua.zxc.cowbot.handler.types.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.ChatService;
import ua.zxc.cowbot.service.TelegramService;

import static java.util.Objects.isNull;
import static ua.zxc.cowbot.utils.Constants.GROUP_NAME_UPDATED;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.CHAT_NEED_REGISTRATION;
import static ua.zxc.cowbot.utils.Constants.SPECIFY_GROUP_NAME;
import static ua.zxc.cowbot.utils.TelegramHelper.getChatName;

@Handler(value = "SET_GROUP_NAME", commands = "/g")
@Slf4j
@RequiredArgsConstructor
public class SetGroupNameHandler implements HandlerStrategy {

    private final ChatService chatService;
    private final TelegramService telegramService;

    @Override
    public SendMessage handleMessage(Update update) {
        telegramService.deleteMessage(update.getMessage());
        String messageText;
        try {
            messageText = updateChatGroupNameAndReturnMessageText(update);
        } catch (RuntimeException e) {
            log.warn("Can not to set a group name for chat with id: {}", update.getMessage().getChatId(), e);
            messageText = SPECIFY_GROUP_NAME;
        }
        return SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text(messageText)
                .parseMode(HTML)
                .build();
    }

    private String updateChatGroupNameAndReturnMessageText(Update update) {
        ChatDTO chatToUpdate = createChatToUpdate(update.getMessage());
        ChatDTO chatDTO = chatService.updateChat(chatToUpdate);
        if (isNull(chatDTO)) {
            log.warn("Can not to set a group name for chat with id: {}", chatToUpdate.getId());
            return CHAT_NEED_REGISTRATION;
        }
        return String.format(GROUP_NAME_UPDATED, chatDTO.getGroupName());
    }

    private ChatDTO createChatToUpdate(Message message) {
        return ChatDTO.builder()
                .id(message.getChatId())
                .name(getChatName(message.getChat()))
                .groupName(parseGroupName(message.getText()))
                .build();
    }

    private String parseGroupName(String text) {
        String groupName = text.substring(3);
        if (groupName.length() < 4) {
            throw new RuntimeException("Cannot parse group name from text = ['" + text + "']");
        }
        return groupName;
    }
}
