package ua.zxc.cowbot.service;

import ua.zxc.cowbot.dto.QueueTaskDTO;

public interface QueueTaskService {

    QueueTaskDTO getQueueTaskByUserIdAndChatId(Long userId, Long chatId);

    QueueTaskDTO insertQueueTask(QueueTaskDTO queueTaskDTO);

    QueueTaskDTO updateQueueTask(QueueTaskDTO queueTaskDTO);

    boolean deleteQueueTask(Long queueTaskId);

    boolean existsByUserIdAndChatId(Long userId, Long chatId);
}
