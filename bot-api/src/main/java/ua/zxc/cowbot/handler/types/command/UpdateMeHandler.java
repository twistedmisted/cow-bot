package ua.zxc.cowbot.handler.types.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.TelegramService;
import ua.zxc.cowbot.service.UserService;

import static java.util.Objects.isNull;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.CHAT_NEED_REGISTRATION;
import static ua.zxc.cowbot.utils.TelegramHelper.createUserDto;
import static ua.zxc.cowbot.utils.TelegramUtils.mentionUser;

@Handler(value = "UPDATE_ME", commands = "/updateme")
@Slf4j
@RequiredArgsConstructor
public class UpdateMeHandler implements HandlerStrategy {

    private final UserService userService;
    private final TelegramService telegramService;

    @Override
    public SendMessage handleMessage(Update update) {
        telegramService.deleteMessage(update.getMessage());
        String messageText;
        User userFrom = update.getMessage().getFrom();
        try {
            UserDTO userDto = createUserDto(userFrom);
            userDto = userService.updateUser(userDto);
            if (isNull(userDto)) {
                messageText = CHAT_NEED_REGISTRATION;
            } else {
                messageText = mentionUser(userDto) + "<b>, інформація успішно оновлена.</b>";
            }
        } catch (RuntimeException e) {
            log.warn("Cannot update user information: {}", userFrom);
            messageText = mentionUser(userFrom) + "<b>, не вдалося оновити інформацію.</b>";
        }
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(messageText)
                .parseMode(HTML)
                .build();
    }
}
