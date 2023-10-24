//package ua.zxc.cowbot.service.integration;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.jdbc.Sql;
//import ua.zxc.cowbot.dto.ChatDTO;
//import ua.zxc.cowbot.helper.ChatInitializr;
//import ua.zxc.cowbot.service.impl.ChatServiceImpl;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.springframework.test.context.jdbc.Sql.ExecutionPhase;
//
//@SpringBootTest
//@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
//@Sql(
//        value = {
//                "classpath:/database/clear-tables.sql",
//                "classpath:/database/insert-user.sql",
//                "classpath:/database/insert-chat.sql",
//                "classpath:/database/insert-user-chat.sql",
//        },
//        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {"classpath:/database/clear-tables.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
//public class ChatServiceImplIntTest {
//
//    @Autowired
//    private ChatServiceImpl chatService;
//
//    @Test
//    public void getChatByIdWithExistingChatIdShouldReturnChatEntity() {
//        ChatDTO actualChatById = chatService.getChatById(1L);
//
//        assertEquals(1L, actualChatById.getId());
//        assertEquals("first chat", actualChatById.getName());
//        assertNull(actualChatById.getGroupName());
//    }
//
//    @Test
//    public void getChatByIdWithNotExistingChatIdShouldReturnNull() {
//        ChatDTO actualChatEntity = chatService.getChatById(0L);
//
//        assertNull(actualChatEntity);
//    }
//
//    @Test
//    public void getAllByUsersIdWithExistingUserIdShouldReturnNotEmptyChatList() {
//        List<ChatDTO> actualChatListByUserId = chatService.getAllByUsersId(3L);
//
//        assertFalse(actualChatListByUserId.isEmpty());
//        assertEquals(3, actualChatListByUserId.size());
//    }
//
//    @Test
//    public void getAllByUsersIdWithNotExistingUserIdShouldReturnEmptyChatList() {
//        List<ChatDTO> actualChatListByUserId = chatService.getAllByUsersId(0L);
//
//        assertTrue(actualChatListByUserId.isEmpty());
//    }
//
//    @Test
//    public void insertChatWithNotExistingIdShouldReturnNewChat() {
//        ChatDTO chatDTO = ChatInitializr.createDTOWithCustomId(100);
//
//        ChatDTO actualChat = chatService.insertChat(chatDTO);
//
//        assertEquals(chatDTO.getId(), actualChat.getId());
//        assertEquals(chatDTO.getName(), actualChat.getName());
//        assertEquals(chatDTO.getGroupName(), actualChat.getGroupName());
//    }
//
//    @Test
//    public void insertChatWithExistingIdShouldReturnNull() {
//        ChatDTO chatDTO = ChatInitializr.createDTOWithCustomId(1L);
//
//        ChatDTO actualChat = chatService.insertChat(chatDTO);
//
//        assertNull(actualChat);
//    }
//
//    @Test
//    public void updateChatWithExistingIdShouldReturnUpdatedChat() {
//        ChatDTO chatDTO = ChatInitializr.createDTOWithCustomId(1L);
//
//        ChatDTO oldChat = chatService.getChatById(1L);
//        ChatDTO actualChat = chatService.updateChat(chatDTO);
//
//        assertEquals(chatDTO.getId(), actualChat.getId());
//        assertEquals(chatDTO.getName(), actualChat.getName());
//        assertEquals(chatDTO.getGroupName(), actualChat.getGroupName());
//
//        assertEquals(oldChat.getId(), actualChat.getId());
//        assertNotEquals(oldChat.getName(), actualChat.getName());
//        assertNotEquals(oldChat.getGroupName(), actualChat.getGroupName());
//    }
//
//    @Test
//    public void updateChatWithNotExistingIdShouldReturnNull() {
//        ChatDTO chatDTO = ChatInitializr.createDTOWithCustomId(100);
//
//        ChatDTO actualChat = chatService.updateChat(chatDTO);
//
//        assertNull(actualChat);
//    }
//
////    @Test
////    public void getGroupNameByChatIdWithExistingChatIdAndExistingGroupNameShouldReturnGroupName() {
////        String actualGroupNameByChatId = chatService.getGroupNameByChatId(2L);
////
////        assertNotNull(actualGroupNameByChatId);
////        assertEquals("XX-00", actualGroupNameByChatId);
////    }
////
////    @Test
////    public void getGroupNameByChatIdWithExistingChatIdAndNotExistingGroupNameShouldReturnNull() {
////        String actualGroupNameByChatId = chatService.getGroupNameByChatId(1L);
////
////        assertNull(actualGroupNameByChatId);
////    }
////
////    @Test
////    public void getGroupNameByChatIdWithNotExistingChatIdShouldReturnNull() {
////        String actualGroupNameByChatId = chatService.getGroupNameByChatId(100L);
////
////        assertNull(actualGroupNameByChatId);
////    }
//}
