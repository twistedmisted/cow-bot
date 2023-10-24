package ua.zxc.cowbot.handler.types.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.exception.MissedValueException;
import ua.zxc.cowbot.exception.NeededRegistrationException;
import ua.zxc.cowbot.handler.annotation.Handler;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.scheduleapi.exception.JSONObjectConvertException;
import ua.zxc.cowbot.scheduleapi.exception.PairNotFoundException;
import ua.zxc.cowbot.scheduleapi.service.ScheduleService;
import ua.zxc.cowbot.service.ChatService;
import ua.zxc.cowbot.service.TelegramService;

import static java.util.Objects.isNull;
import static ua.zxc.cowbot.utils.Constants.HTML;
import static ua.zxc.cowbot.utils.Constants.CHAT_NEED_REGISTRATION;
import static ua.zxc.cowbot.utils.Constants.TRY_AGAIN_WHAT_SOMETHING;

@Handler(value = "TOMORROW_PAIRS", commands = "/tomorrow")
@Slf4j
@RequiredArgsConstructor
public class TomorrowPairsHandler implements HandlerStrategy {

    private final ScheduleService scheduleService;
    private final ChatService chatService;
    private final TelegramService telegramService;

    @Override
    public SendMessage handleMessage(Update update) {
        telegramService.deleteMessage(update.getMessage());
        String messageText;
        Long chatId = update.getMessage().getChatId();
        try {
            messageText = processFindingTomorrowPairsForChat(chatId);
        } catch (JSONObjectConvertException e) {
            messageText = "Сьогодні пар нема.";
        } catch (PairNotFoundException e) {
            messageText = "Пари нема, можна відпочивати.";
        } catch (NeededRegistrationException e) {
            messageText = CHAT_NEED_REGISTRATION;
        } catch (MissedValueException e) {
            messageText = "Вкажіть ім'я групи, використовуючи команду <b>/g</b>, наприклад, <b>'/g АА-09'</b>.";
        } catch (RuntimeException e) {
            log.warn("Cannot to get pair now for chat with id: {}", chatId);
            messageText = String.format(TRY_AGAIN_WHAT_SOMETHING, "опрацювати запит");
        }
        return createMessageForTomorrowPairs(chatId, messageText);
    }

    private String processFindingTomorrowPairsForChat(Long chatId) throws PairNotFoundException, JSONObjectConvertException {
        String groupNameByChatId = getGroupNameByChatId(chatId);
        String tomorrowPairs = scheduleService.getTomorrowPairs(groupNameByChatId);
        if (tomorrowPairs.isBlank()) {
            throw new RuntimeException("Pairs now string is blank");
        }
        return tomorrowPairs;
    }

    private String getGroupNameByChatId(Long chatId) {
        ChatDTO chatById = chatService.getChatById(chatId);
        throwExceptionIfChatIsNull(chatId, chatById);
        String groupName = chatById.getGroupName();
        throwExceptionIfGroupNameNotExist(chatId, groupName);
        return groupName;
    }

    private static void throwExceptionIfChatIsNull(Long chatId, ChatDTO chatById) {
        if (isNull(chatById)) {
            log.warn("Cannot get the chat with id: {}", chatId);
            throw new NeededRegistrationException("Cannot get chat by id: " + chatId);
        }
    }

    private static void throwExceptionIfGroupNameNotExist(Long chatId, String groupName) {
        if (groupName == null || groupName.isBlank()) {
            log.warn("Cannot get a group name for chat with id: {}", chatId);
            throw new MissedValueException("Cannot get a group name for chat with id: " + chatId);
        }
    }

    private SendMessage createMessageForTomorrowPairs(Long chatId, String messageText) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .parseMode(HTML)
                .build();

    }
}
