package ua.zxc.cowbot.service;

import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.QueueDTO;

import java.util.List;

public interface QueueService {

    QueueDTO getQueueById(Long queueId);

    List<QueueDTO> getAllByChatId(Long chatId);

    PageBO<QueueDTO> getAllByChatId(Long chatId, Integer pageNum, Integer pageSize);

    QueueDTO insertQueue(QueueDTO queueDTO);

    QueueDTO updateQueue(QueueDTO queueDTO);

    QueueDTO updateQueueSizeByQueueId(Long queueId, Integer size);

    QueueDTO deleteQueue(Long queueId);

    boolean existsById(Long queueId);
}
