package ua.zxc.cowbot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.handler.impl.CallbackHandler;
import ua.zxc.cowbot.handler.impl.MessageHandler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.service.ChatService;
import ua.zxc.cowbot.service.QueueTaskService;

import static ua.zxc.cowbot.utils.TelegramHelper.createChatDto;
import static ua.zxc.cowbot.utils.TelegramUtils.isCallbackQueryUpdate;
import static ua.zxc.cowbot.utils.TelegramUtils.isMessageUpdate;
import static ua.zxc.cowbot.utils.TelegramUtils.isMyChatMemberUpdate;
import static ua.zxc.cowbot.utils.TelegramUtils.messageHasSticker;
import static ua.zxc.cowbot.utils.TelegramUtils.messageHasText;

@Service
@CacheConfig(cacheNames = "usersCacheManager")
@RequiredArgsConstructor
public class MainHandler {

    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;
    private final QueueTaskService queueTasksService;
    private final BeanFactory beanFactory;
    private final ChatService chatService;

    public SendMessage createMessage(Update update) {
        if (queueTaskExistsByUserIdAndChatId(update)) {
            return beanFactory.getBean("QUEUE_TASK", HandlerStrategy.class).handleMessage(update);
        }
        if (isMessageUpdate(update.getMessage())) {
            return processMessageUpdate(update);
        }
        if (isCallbackQueryUpdate(update)) {
            return processCallbackQueryUpdate(update);
        }
        if (isMyChatMemberUpdate(update.getMyChatMember())) {
            // TODO: maybe add message about status of registration
            registerChatIfNotExist(update.getMyChatMember());
        }
        return new SendMessage();
    }

    private void registerChatIfNotExist(ChatMemberUpdated myChatMember) {
        if (chatService.existsById(myChatMember.getChat().getId())) {
            ChatDTO chatById = chatService.getChatById(myChatMember.getChat().getId());
            ChatDTO chatDto = createChatDto(myChatMember.getChat());
            chatDto.setGroupName(chatById.getGroupName());
            chatService.updateChat(chatDto);
        } else {
            chatService.insertChat(createChatDto(myChatMember.getChat()));
        }
    }

    private SendMessage processCallbackQueryUpdate(Update update) {
        return callbackHandler.createSendMessage(update);
    }

    private SendMessage processMessageUpdate(Update update) {
        if (messageHasText(update)) {
            return messageHandler.createSendMessage(update);
        } else if (messageHasSticker(update)) {
            return beanFactory.getBean("STICKER", HandlerStrategy.class).handleMessage(update);
        }
        return new SendMessage();
    }

    private boolean queueTaskExistsByUserIdAndChatId(Update update) {
        long userId = 0L;
        Long chatId = 0L;
        if (update.getMessage() != null && update.getMessage().hasText()) {
            userId = update.getMessage().getFrom().getId();
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            userId = update.getCallbackQuery().getFrom().getId();
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        if (userId != 0 && chatId != 0) {
            return queueTasksService.existsByUserIdAndChatId(userId, chatId);
        }
        return false;
    }
}
