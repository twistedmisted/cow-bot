package ua.zxc.cowbot.handler.types.other;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ua.zxc.cowbot.dto.RespectDTO;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.RespectService;
import ua.zxc.cowbot.service.UserService;

import static java.util.Objects.isNull;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;
import static ua.zxc.cowbot.utils.TelegramUtils.isFromBot;

@Handler(value = "PAY_DISRESPECT")
@Slf4j
public class PayDisrespectHandler implements HandlerStrategy {

    private final RespectService respectService;

    private final UserService userService;

    public PayDisrespectHandler(RespectService respectService, UserService userService) {
        this.respectService = respectService;
        this.userService = userService;
    }

    @Override
    public SendMessage handleMessage(Update update) {
        Message message = update.getMessage();
        try {
            return payDisrespectToUserAndCreateMessageToSend(message);
        } catch (RuntimeException e) {
            log.warn("Cannot pay respect to user, something went wrong", e);
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(String.format(TRY_AGAIN_WHAT_SOMETHING, "забрати вповажайку"))
                    .build();
        }
    }

    private SendMessage payDisrespectToUserAndCreateMessageToSend(Message message) {
        Message replyToMessage = message.getReplyToMessage();
        if (isNull(replyToMessage) || isRepliedHimself(message) || isFromBot(replyToMessage)) {
            return new SendMessage();
        }
        payDisrespectToUser(message);
        return createMessageToSend(message);
    }

    private boolean isRepliedHimself(Message message) {
        return message.getReplyToMessage().getFrom().equals(message.getFrom());
    }

    private void payDisrespectToUser(Message message) {
        User userToRespect = message.getReplyToMessage().getFrom();
        if (isUserRegistered(userToRespect.getId())) {
            respectService.payDisrespect(createRespectDTOFromMessage(message));
        }
    }

    private boolean isUserRegistered(Long userId) {
        return userService.existsUserById(userId);
    }

    private RespectDTO createRespectDTOFromMessage(Message message) {
        return RespectDTO.builder()
                .userId(message.getReplyToMessage().getFrom().getId())
                .chatId(message.getChatId())
                .build();
    }

    private SendMessage createMessageToSend(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(createMessageText(message))
                .parseMode(HTML)
                .build();
    }

    private String createMessageText(Message message) {
        return "<pre>" +
                message.getFrom().getFirstName() +
                " " +
                "забирає вповажайку" +
                " " +
                message.getReplyToMessage().getFrom().getFirstName() +
                "</pre>";
    }
}
