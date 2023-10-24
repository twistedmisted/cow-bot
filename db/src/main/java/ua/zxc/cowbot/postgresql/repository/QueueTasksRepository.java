package ua.zxc.cowbot.postgresql.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zxc.cowbot.postgresql.entity.QueueTaskEntity;

import java.util.Optional;

@Repository
public interface QueueTasksRepository extends CrudRepository<QueueTaskEntity, Long> {

    Optional<QueueTaskEntity> findByUserIdAndChatId(Long userId, Long chatId);

    boolean existsByUserIdAndChatId(Long userId, Long chatId);
}
