package ua.zxc.cowbot.service;

import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.RespectDTO;

import java.util.List;

public interface RespectService {

    RespectDTO getRespectById(Long userId, Long chatId);

    PageBO<RespectDTO> getAllByChatId(Long chatId, Integer pageNum, Integer pageSize);

    void payRespect(RespectDTO respectDTO);

    void payDisrespect(RespectDTO respectDTO);
}
