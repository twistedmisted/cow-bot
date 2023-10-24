package ua.zxc.cowbot.service;


import ua.zxc.cowbot.dto.ChatDTO;

import java.util.List;

public interface ChatService {

    ChatDTO getChatById(Long chatId);

    List<ChatDTO> getAllByUsersId(Long userId);

    ChatDTO insertChat(ChatDTO chatDTO);

    ChatDTO updateChat(ChatDTO chatDTO);

    boolean existsById(Long chatId);
}