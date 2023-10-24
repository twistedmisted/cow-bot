package ua.zxc.cowbot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.zxc.cowbot.ServiceSpringBootTest;
import ua.zxc.cowbot.dto.UserDTO;
import ua.zxc.cowbot.helper.UserInitializr;
import ua.zxc.cowbot.postgresql.entity.UserEntity;
import ua.zxc.cowbot.postgresql.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceImplTest extends ServiceSpringBootTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private static final UserEntity USER_ENTITY = UserInitializr.createUserEntity();

    private static final UserDTO USER_DTO = UserInitializr.createUserDTO();

    @Test
    public void getUserByIdWithExistingIdShouldReturnUserEntity() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(USER_ENTITY));

        UserDTO actualUser = userService.getUserById(1L);

        assertNotNull(actualUser);
        assertEquals(USER_ENTITY.getId(), actualUser.getId());

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getUserByIdWithNotExistingUserIdShouldReturnNull() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserDTO actualUser = userService.getUserById(1L);

        assertNull(actualUser);

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getAllByChatIdWithExistingChatIdShouldReturnListOfUsers() {
        when(userRepository.findAllByChatsId(anyLong())).thenReturn(Collections.singleton(USER_ENTITY));

        List<UserDTO> actualUsersByChatId = userService.getAllByChatId(1L);

        assertNotNull(actualUsersByChatId);
        assertFalse(actualUsersByChatId.isEmpty());

        verify(userRepository, times(1)).findAllByChatsId(anyLong());
    }

    @Test
    public void getAllByChatIdWithNotExistingChatIdShouldReturnEmptySet() {
        when(userRepository.findAllByChatsId(anyLong())).thenReturn(Collections.emptySet());

        List<UserDTO> actualUsersByChatId = userService.getAllByChatId(1L);

        assertNotNull(actualUsersByChatId);
        assertTrue(actualUsersByChatId.isEmpty());

        verify(userRepository, times(1)).findAllByChatsId(anyLong());
    }

    @Test
    public void insertUserWithNotExistingUserIdShouldReturnNewUserEntity() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(USER_ENTITY);

        UserDTO actualUser = userService.insertUser(USER_DTO);

        assertNotNull(actualUser);
        assertEquals(USER_DTO.getFirstName(), actualUser.getFirstName());

        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void insertUserWithExistingUserIdShouldReturnNull() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        UserDTO actualUser = userService.insertUser(USER_DTO);

        assertNull(actualUser);

        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, times(0)).save(any(UserEntity.class));
    }

    @Test
    public void updateUserWithExistingUserIdShouldReturnUpdatedUserEntity() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.save(any(UserEntity.class))).thenReturn(USER_ENTITY);

        UserDTO actualUser = userService.updateUser(USER_DTO);

        assertNotNull(actualUser);
        assertEquals(USER_DTO.getFirstName(), actualUser.getFirstName());

        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void updateUserWithNotExistingUserIdShouldReturnNull() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        UserDTO actualUser = userService.updateUser(USER_DTO);

        assertNull(actualUser);

        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, times(0)).save(any(UserEntity.class));
    }
}