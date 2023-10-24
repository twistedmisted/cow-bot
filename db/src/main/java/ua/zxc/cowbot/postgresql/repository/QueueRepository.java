package ua.zxc.cowbot.postgresql.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.zxc.cowbot.postgresql.entity.QueueEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends CrudRepository<QueueEntity, Long> {

    @Query(value = "SELECT q FROM QueueEntity q " +
            "LEFT JOIN FETCH q.places p " +
            "WHERE q.id = :id")
    Optional<QueueEntity> findById(@Param("id") Long id);

    List<QueueEntity> findAllByChatIdOrderByIdDesc(Long chatId);

    Page<QueueEntity> findAllByChatIdOrderByIdDesc(Pageable pageable, Long chatId);

    @Transactional
    List<QueueEntity> deleteQueueById(Long id);

    boolean existsByChatIdAndName(Long chatId, String queueName);

    boolean existsByIdNotAndChatIdAndName(Long id, Long chatId, String groupName);
}
