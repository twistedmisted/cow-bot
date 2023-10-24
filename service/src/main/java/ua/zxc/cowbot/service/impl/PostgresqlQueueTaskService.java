package ua.zxc.cowbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.zxc.cowbot.dto.QueueTaskDTO;
import ua.zxc.cowbot.postgresql.entity.QueueTaskEntity;
import ua.zxc.cowbot.postgresql.repository.QueueTasksRepository;
import ua.zxc.cowbot.service.QueueTaskService;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class PostgresqlQueueTaskService implements QueueTaskService {

    private final QueueTasksRepository queueTasksRepository;
    private final QueueTaskMapper queueTaskMapper;

    public PostgresqlQueueTaskService(QueueTasksRepository queueTasksRepository, QueueTaskMapper queueTaskMapper) {
        this.queueTasksRepository = queueTasksRepository;
        this.queueTaskMapper = queueTaskMapper;
    }

    @Override
    public QueueTaskDTO getQueueTaskByUserIdAndChatId(Long userId, Long chatId) {
        log.info("Getting a queue task with user id {} and chat id {}", userId, chatId);
        QueueTaskEntity queueTaskEntity = queueTasksRepository.findByUserIdAndChatId(userId, chatId).orElse(null);
        if (isNull(queueTaskEntity)) {
            log.info("Cannot get a queue with user id {} and chat id {}", userId, chatId);
            return null;
        }
        log.info("The queue task with user id {} and chat id {} was successfully found", userId, chatId);
        return queueTaskMapper.entityToDto(queueTaskEntity);
    }

    @Override
    public QueueTaskDTO insertQueueTask(QueueTaskDTO queueTaskDTO) {
        log.info("Inserting a queue task: {}", queueTaskDTO);
        if (existsByUserIdAndChatId(queueTaskDTO.getUserId(), queueTaskDTO.getChatId())) {
            log.info("The queue task with user id {} and chat id {} already exists",
                    queueTaskDTO.getUserId(), queueTaskDTO.getChatId());
            return null;
        }
        QueueTaskEntity queueTaskEntity = queueTaskMapper.dtoToEntity(queueTaskDTO);
        queueTaskEntity = queueTasksRepository.save(queueTaskEntity);
        log.info("The queue task with user id {} and chat id {} successfully inserted",
                queueTaskDTO.getUserId(), queueTaskDTO.getChatId());
        return queueTaskMapper.entityToDto(queueTaskEntity);
    }

    @Override
    public QueueTaskDTO updateQueueTask(QueueTaskDTO queueTaskDTO) {
        log.info("Updating a queue task with user id {} and chat id {}",
                queueTaskDTO.getUserId(), queueTaskDTO.getChatId());
        if (!existsByUserIdAndChatId(queueTaskDTO.getUserId(), queueTaskDTO.getChatId())) {
            log.info("The queue task with user id {} and chat id {} does not exist",
                    queueTaskDTO.getUserId(), queueTaskDTO.getChatId());
            return null;
        }
        QueueTaskEntity queueTaskEntity = queueTaskMapper.dtoToEntity(queueTaskDTO);
        queueTaskEntity = queueTasksRepository.save(queueTaskEntity);
        log.info("The queue task with user id {} and chat id {} successfully updated",
                queueTaskDTO.getUserId(), queueTaskDTO.getChatId());
        return queueTaskMapper.entityToDto(queueTaskEntity);
    }

    @Override
    public boolean deleteQueueTask(Long queueTaskId) {
        log.info("Removing queue task by queue task id: {}", queueTaskId);
        try {
            queueTasksRepository.deleteById(queueTaskId);
            log.info("The queue task with queue task id: {} was successfully removed", queueTaskId);
            return true;
        } catch (RuntimeException e) {
            log.info("The queue task with queue task id: {} was not removed. Probably it does not exist",
                    queueTaskId, e);
            return false;
        }
    }

    @Override
    public boolean existsByUserIdAndChatId(Long userId, Long chatId) {
        log.info("Check if exists queue task user by id: {} and chat id: {}", userId, chatId);
        return queueTasksRepository.existsByUserIdAndChatId(userId, chatId);
    }
}
