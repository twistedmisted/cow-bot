package ua.zxc.cowbot.postgresql.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zxc.cowbot.postgresql.entity.RespectEntity;
import ua.zxc.cowbot.postgresql.entity.embeddedid.UserChatId;

@Repository
public interface RespectRepository extends JpaRepository<RespectEntity, UserChatId> {

    Page<RespectEntity> findAllByChatIdOrderByNumberThisMonthDesc(Pageable pageable, Long chatId);
}
