package ua.zxc.cowbot.utils;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.dto.UserDTO;

public class TelegramHelper {

    public static ChatDTO createChatDto(Chat chat) {
        return ChatDTO.builder()
                .id(chat.getId())
                .name(getChatName(chat))
                .build();
    }

    public static String getChatName(Chat chat) {
        String name = chat.getTitle();
        if (name == null) {
            name = chat.getFirstName();
        }
        return name;
    }

    public static UserDTO createUserDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUserName())
                .firstName(user.getFirstName())
                .build();
    }
}
