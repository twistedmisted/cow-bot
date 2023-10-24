package ua.zxc.cowbot.service;

import ua.zxc.cowbot.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO getUserById(Long userId);

    List<UserDTO> getAllByChatId(Long chatId);

    List<UserDTO> getAllByChatId(Long chatId, Integer pageNum, Integer pageSize);

    UserDTO insertUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO userDTO);

    boolean existsUserById(Long userId);
}
