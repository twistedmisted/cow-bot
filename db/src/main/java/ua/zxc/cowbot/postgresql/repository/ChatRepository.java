package ua.zxc.cowbot.postgresql.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zxc.cowbot.postgresql.entity.ChatEntity;

import java.util.Set;

@Repository
public interface ChatRepository extends CrudRepository<ChatEntity, Long> {

    Set<ChatEntity> findAllByUsersId(long userId);
}
