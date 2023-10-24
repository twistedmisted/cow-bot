//package ua.zxc.cowbot.service.integration;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.jdbc.Sql;
//import ua.zxc.cowbot.dto.UserDTO;
//import ua.zxc.cowbot.helper.UserInitializr;
//import ua.zxc.cowbot.service.UserService;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
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
//        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {"classpath:/database/clear-tables.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//public class UserServiceImplIntTest {
//
//    @Autowired
//    private UserService userService;
//
//    @Test
//    public void getUserByIdWithExistingUserIdShouldReturnUserEntity() {
//        UserDTO actualUserById = userService.getUserById(1L);
//
//        assertNotNull(actualUserById);
//        assertEquals(Long.valueOf(1), actualUserById.getId());
//    }
//
//    @Test
//    public void getUserByIdWithNotExistingUserIdShouldReturnNull() {
//        UserDTO actualUserById = userService.getUserById(0L);
//
//        assertNull(actualUserById);
//    }
//
//    @Test
//    public void getAllByChatIdWithExistingChatIdShouldReturnNotEmptyUserList() {
//        List<UserDTO> actualUsersByChatId = userService.getAllByChatId(3L);
//
//        assertFalse(actualUsersByChatId.isEmpty());
//        assertEquals(2, actualUsersByChatId.size());
//    }
//
//    @Test
//    public void getAllByChatIdWithNotExistingChatIdShouldReturnEmptyUserList() {
//        List<UserDTO> actualUsersByChatId = userService.getAllByChatId(0L);
//
//        assertTrue(actualUsersByChatId.isEmpty());
//    }
//
//    @Test
//    public void insertUserWithNotExistingUserIdShouldReturnNewUser() {
//        long userId = 100;
//        UserDTO userToInsert = UserInitializr.createUserDTOWithCustomId(userId);
//
//        UserDTO actualUser = userService.insertUser(userToInsert);
//
//        assertNotNull(actualUser);
//        assertEquals(userId, actualUser.getId());
//    }
//
//    @Test
//    public void insertUserWithExistingUserIdShouldReturnNull() {
//        UserDTO userToInsert = UserInitializr.createUserDTO();
//
//        UserDTO actualUser = userService.insertUser(userToInsert);
//
//        assertNull(actualUser);
//    }
//
//    @Test
//    public void updateUserWithExistingUserIdShouldReturnUpdatedUser() {
//        UserDTO userToUpdate = UserInitializr.createUserDTO();
//
//        UserDTO actualUser = userService.updateUser(userToUpdate);
//
//        assertNotNull(actualUser);
//        assertEquals(userToUpdate, actualUser);
//    }
//
//    @Test
//    public void updateUserWithNotExistingUserIdShouldReturnNull() {
//        UserDTO userToUpdate = UserInitializr.createUserDTOWithCustomId(0);
//
//        UserDTO actualUser = userService.updateUser(userToUpdate);
//
//        assertNull(actualUser);
//    }
//}
