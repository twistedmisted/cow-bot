package ua.zxc.cowbot.handler.types.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.RegistrationService;
import ua.zxc.cowbot.service.TelegramService;

import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.REGISTRATION_ERROR;
import static ua.zxc.cowbot.utils.Constants.SUCCESSFUL_REGISTRATION;
import static ua.zxc.cowbot.utils.Constants.USER_ALREADY_REGISTERED;
import static ua.zxc.cowbot.utils.TelegramHelper.createChatDto;
import static ua.zxc.cowbot.utils.TelegramHelper.createUserDto;
import static ua.zxc.cowbot.utils.TelegramUtils.mentionUser;

@Handler(value = "REGISTRATION", commands = {"/start", "/registration"})
@Slf4j
@RequiredArgsConstructor
public class RegistrationHandler implements HandlerStrategy {

    private final RegistrationService registrationService;
    private final TelegramService telegramService;

    @Override
    public SendMessage handleMessage(Update update) {
        telegramService.deleteMessage(update.getMessage());
        String messageText;
        try {
            messageText = mentionUser(update.getMessage().getFrom()) + processRegistration(update.getMessage());
        } catch (RuntimeException e) {
            log.warn("Cannot register user with chat", e);
            messageText = REGISTRATION_ERROR;
        }
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(messageText)
                .parseMode(HTML)
                .build();
    }

    private String processRegistration(Message message) {
        UserDTO userToSave = createUserDto(message.getFrom());
        ChatDTO chatToSave = createChatDto(message.getChat());
        boolean registered = registrationService.registration(userToSave, chatToSave);
        return getMessageTextByRegisteredStatus(registered);
    }

    private String getMessageTextByRegisteredStatus(boolean registered) {
        if (registered) {
            return SUCCESSFUL_REGISTRATION;
        }
        return USER_ALREADY_REGISTERED;
    }
}
