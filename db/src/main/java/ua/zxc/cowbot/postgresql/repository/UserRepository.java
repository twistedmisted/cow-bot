package ua.zxc.cowbot.postgresql.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zxc.cowbot.postgresql.entity.UserEntity;

import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Set<UserEntity> findAllByChatsId(Long chatId);

    Page<UserEntity> findAllByChatsId(Pageable pageable, Long chatId);

    boolean existsByIdAndChatsId(Long userId, Long chatId);
}