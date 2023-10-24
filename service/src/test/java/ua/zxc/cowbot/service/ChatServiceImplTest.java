package ua.zxc.cowbot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.zxc.cowbot.ServiceSpringBootTest;
import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.helper.ChatInitializr;
import ua.zxc.cowbot.postgresql.entity.ChatEntity;
import ua.zxc.cowbot.postgresql.repository.ChatRepository;
import ua.zxc.cowbot.service.impl.ChatServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class ChatServiceImplTest extends ServiceSpringBootTest {

    @MockBean
    private ChatRepository chatRepository;

    @Autowired
    private ChatServiceImpl chatService;

    private static final ChatDTO CHAT_DTO = ChatInitializr.createDTO();

    private static final ChatEntity CHAT_ENTITY = ChatInitializr.createEntity();

    @Test
    public void getChatByIdWithExistingIdShouldReturnChatEntity() {
        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(CHAT_ENTITY));

        ChatDTO actualChat = chatService.getChatById(1L);

        assertEquals(CHAT_ENTITY.getName(), actualChat.getName());
    }

    @Test
    public void getChatByIdWithNotExistingIdShouldReturnNull() {
        when(chatRepository.findById(anyLong())).thenReturn(Optional.empty());

        ChatDTO actualChat = chatService.getChatById(1L);

        assertNull(actualChat);
    }

    @Test
    public void getAllByUserIdWithExistingChatsByUserIdShouldReturnChatsSet() {
        when(chatRepository.findAllByUsersId(anyLong())).thenReturn(Collections.singleton(CHAT_ENTITY));

        List<ChatDTO> actualChatsByUserId = chatService.getAllByUsersId(1L);

        assertNotNull(actualChatsByUserId);
        assertTrue(actualChatsByUserId.contains(CHAT_DTO));
    }

    @Test
    public void getAllByUserIdWithNotExistingChatsByUserIdShouldReturnEmptySet() {
        when(chatRepository.findAllByUsersId(anyLong())).thenReturn(Collections.emptySet());

        List<ChatDTO> actualChatsByUserId = chatService.getAllByUsersId(1L);

        assertNotNull(actualChatsByUserId);
        assertTrue(actualChatsByUserId.isEmpty());
    }

    @Test
    public void insertChatWithNotExistingIdShouldReturnNewChatEntity() {
        when(chatRepository.existsById(anyLong())).thenReturn(false);
        when(chatRepository.save(any(ChatEntity.class))).thenReturn(CHAT_ENTITY);

        ChatDTO actualChat = chatService.insertChat(CHAT_DTO);

        assertEquals(CHAT_ENTITY.getName(), actualChat.getName());
    }

    @Test
    public void insertChatWithExistingChatIdShouldReturnNull() {
        when(chatRepository.existsById(anyLong())).thenReturn(true);

        ChatDTO actualChat = chatService.insertChat(CHAT_DTO);

        assertNull(actualChat);
    }

    @Test
    public void updateChatWithExistingChatIdShouldBeOk() {
        when(chatRepository.existsById(anyLong())).thenReturn(true);
        when(chatRepository.save(any(ChatEntity.class))).thenReturn(CHAT_ENTITY);

        ChatDTO actualChat = chatService.updateChat(CHAT_DTO);

        assertEquals(CHAT_ENTITY.getName(), actualChat.getName());
    }

    @Test
    public void updateChatWithNotExistingChatIdShouldReturnNull() {
        when(chatRepository.existsById(anyLong())).thenReturn(false);

        ChatDTO actualChat = chatService.updateChat(CHAT_DTO);

        assertNull(actualChat);
    }

//    @Test
//    public void getGroupNameChatIdWithExistingChatIdAndExistingGroupNameShouldReturnGroupName() {
//        ChatEntity chatEntity = ChatInitializr.createChatEntityWithGroupName();
//
//        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(chatEntity));
//
//        String actualGroupNameByChatId = chatService.getGroupNameByChatId(1L);
//
//        assertEquals(chatEntity.getGroupName(), actualGroupNameByChatId);
//    }
//
//    @Test
//    public void getGroupNameByChatIdWithNotExistingChatIdShouldReturnNull() {
//        when(chatRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        String actualGroupNameByChatId = chatService.getGroupNameByChatId(1L);
//
//        assertNull(actualGroupNameByChatId);
//    }
//
//    @Test
//    public void getGroupNameByChatIdWithExistingChatIdAndNotExistingGroupNameShouldReturnNull() {
//        ChatEntity chatEntity = ChatInitializr.createChatEntityWithoutGroupName();
//
//        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(chatEntity));
//
//        String actualGroupNameByChatId = chatService.getGroupNameByChatId(1L);
//
//        assertNull(actualGroupNameByChatId);
//    }
}