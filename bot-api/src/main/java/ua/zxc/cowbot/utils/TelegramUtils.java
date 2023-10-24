package ua.zxc.cowbot.utils;

import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ua.zxc.cowbot.dto.UserDTO;

public class TelegramUtils {

    public static String mentionUser(User user) {
        return String.format("<a href=\"tg://user?id=%d\">%s</a>", user.getId(), user.getFirstName());
    }

    public static String mentionUser(UserDTO user) {
        return String.format("<a href=\"tg://user?id=%d\">%s</a>", user.getId(), user.getFirstName());
    }

    public static String mentionUser(String username, long userId) {
        return String.format("<a href=\"tg://user?id=%d\">%s</a>", userId, username);
    }

    public static boolean isMessageUpdate(Message message) {
        return message != null;
    }

    public static boolean isMyChatMemberUpdate(ChatMemberUpdated chatMemberUpdated) {
        return chatMemberUpdated != null;
    }

    public static boolean messageHasText(Update update) {
        return update.getMessage().hasText();
    }

    public static boolean messageHasSticker(Update update) {
        return update.getMessage().hasSticker();
    }

    public static boolean isCallbackQueryUpdate(Update update) {
        return update.hasCallbackQuery();
    }

    public static boolean isFromBot(Message message) {
        return message.getFrom().getIsBot();
    }
}
