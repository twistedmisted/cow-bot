package ua.zxc.cowbot.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.mapper.impl.LessonMapper;
import ua.zxc.cowbot.postgresql.entity.LessonEntity;
import ua.zxc.cowbot.postgresql.repository.LessonRepository;
import ua.zxc.cowbot.service.LessonService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    private final LessonMapper lessonMapper;

    public LessonServiceImpl(LessonRepository lessonRepository, LessonMapper lessonMapper) {
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
    }

    @Override
    public LessonDTO getLessonById(Long lessonId) {
        log.info("Getting a lesson by id: {}", lessonId);
        LessonEntity lessonEntity = lessonRepository.findById(lessonId).orElse(null);
        if (isNull(lessonEntity)) {
            log.info("The lesson with id {} was not found", lessonId);
            return null;
        }
        log.info("The lesson with id {} was successfully found", lessonId);
        return lessonMapper.entityToDto(lessonEntity);
    }

    @Override
    public List<LessonDTO> getAllByChatId(Long chatId) {
        log.info("Getting all lessons by chat id: {}", chatId);
        List<LessonEntity> lessonEntitiesByChatId = lessonRepository.findAllByChatIdOrderByNameAsc(chatId);
        if (lessonEntitiesByChatId.isEmpty()) {
            log.info("Can not to find lessons by chat id: {}", chatId);
            return new ArrayList<>();
        }
        log.info("The lessons by chat id {} were successfully found", chatId);
        return lessonMapper.entitiesToDtos(lessonEntitiesByChatId);
    }

    @Override
    public PageBO<LessonDTO> getAllByChatId(Long chatId, Integer pageNum, Integer pageSize) {
        log.info("Getting all lessons for chat with id: {} and pageNum: {} and pageSize: {}", chatId, pageNum, pageSize);
        Page<LessonEntity> lessonEntitiesPageByChatId =
                lessonRepository.findAllByChatIdOrderByNameAsc(PageRequest.of(pageNum, pageSize), chatId);
        if (!lessonEntitiesPageByChatId.hasContent()) {
            log.info("Can not to find lessons by chat id: {} and page parameters(pageNum = {} and pageSize = {})",
                    chatId, pageNum, pageSize);
            return new PageBO<>();
        }
        log.info("The lessons by chat id {} and page parameters(pageNum = {} and pageSize = {}) were successfully found",
                chatId, pageNum, pageSize);
        List<LessonDTO> lessonDTOSByChatId = lessonEntitiesPageByChatId.getContent()
                .stream()
                .map(lessonMapper::entityToDto)
                .collect(Collectors.toList());
        return new PageBO<>(lessonDTOSByChatId, lessonEntitiesPageByChatId.getNumber(),
                lessonEntitiesPageByChatId.getTotalPages());
    }

    @Override
    public LessonDTO insertLesson(LessonDTO lessonDTO) {
        log.info("Inserting lesson: {}", lessonDTO);
        if (lessonExistsByNameAndChatId(lessonDTO.getName(), lessonDTO.getChatId())) {
            log.info("The lesson already exists with this name in this chat");
            return null;
        }
        LessonEntity lessonEntityToSave = lessonMapper.dtoToEntity(lessonDTO);
        lessonEntityToSave = lessonRepository.save(lessonEntityToSave);
        return lessonMapper.entityToDto(lessonEntityToSave);
    }

    private boolean lessonExistsByNameAndChatId(String name, Long chatId) {
        return lessonRepository.existsByNameAndChatId(name, chatId);
    }

    @Override
    public LessonDTO updateLesson(LessonDTO lessonDTO) {
        log.info("Updating a lesson with id: {}", lessonDTO.getId());
        if (!existsById(lessonDTO.getId())) {
            log.info("The lesson with id {} does not exist", lessonDTO.getId());
            return null;
        }
        LessonEntity lessonEntity = lessonMapper.dtoToEntity(lessonDTO);
        lessonEntity = lessonRepository.save(lessonEntity);
        log.info("The lesson with id {} successfully updated", lessonDTO.getId());
        return lessonMapper.entityToDto(lessonEntity);
    }

    @Override
    public LessonDTO deleteLesson(Long lessonId) {
        log.info("Removing lesson by id: {}", lessonId);
        try {
            LessonEntity removedLesson = lessonRepository.deleteLessonById(lessonId).get(0);
            if (removedLesson == null) {
                throw new RuntimeException("Cannot remove lesson with id: " + lessonId);
            }
            log.info("The lesson with id {} was successfully removed", lessonId);
            return lessonMapper.entityToDto(removedLesson);
        } catch (RuntimeException e) {
            log.info("The lesson with id {} was not removed. Probably it does not exist", lessonId, e);
            return null;
        }
    }

    @Override
    public boolean existsById(Long lessonId) {
        return lessonRepository.existsById(lessonId);
    }
}