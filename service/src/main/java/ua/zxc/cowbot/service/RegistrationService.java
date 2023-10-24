package ua.zxc.cowbot.service;

import ua.zxc.cowbot.dto.ChatDTO;
import ua.zxc.cowbot.dto.UserDTO;

public interface RegistrationService {

    boolean registration(UserDTO user, ChatDTO chat);
}
