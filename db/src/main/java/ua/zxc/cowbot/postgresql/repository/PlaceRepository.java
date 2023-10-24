package ua.zxc.cowbot.postgresql.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zxc.cowbot.postgresql.entity.PlaceEntity;
import ua.zxc.cowbot.postgresql.entity.embeddedid.UserQueueId;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends CrudRepository<PlaceEntity, UserQueueId> {

    boolean existsByQueueIdAndNumber(Long queueId, Integer number);

    boolean existsByUserIdAndQueueId(Long userId, Long queueId);

    boolean existsByNumber(Integer number);

    @Transactional
    void deleteByQueueIdAndUserId(Long queueId, Long userId);

    List<PlaceEntity> findAllByQueueIdOrderByNumberAsc(Long queueId);

    Optional<PlaceEntity> findFirstByQueueIdOrderByNumberDesc(Long queueId);

    Optional<PlaceEntity> findByUserIdAndQueueId(Long userId, Long queueId);

    Optional<PlaceEntity> findByNumberAndQueueId(Integer number, Long queueId);
}
