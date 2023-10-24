package ua.zxc.cowbot.postgresql.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zxc.cowbot.postgresql.entity.LessonEntity;

import java.util.List;

@Repository
public interface LessonRepository extends CrudRepository<LessonEntity, Long> {

    Page<LessonEntity> findAllByChatIdOrderByNameAsc(Pageable pageable, Long chatId);

    List<LessonEntity> findAllByChatIdOrderByNameAsc(Long chatId);

    boolean existsByNameAndChatId(String name, Long chatId);

    List<LessonEntity> deleteLessonById(Long id);
}
