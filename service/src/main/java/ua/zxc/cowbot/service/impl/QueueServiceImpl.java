package ua.zxc.cowbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.exception.IllegalSizeRangeException;
import ua.zxc.cowbot.exception.ObjectAlreadyExistsException;
import ua.zxc.cowbot.exception.SizeValueOutOfBoundsException;
import ua.zxc.cowbot.postgresql.entity.PlaceEntity;
import ua.zxc.cowbot.postgresql.entity.QueueEntity;
import ua.zxc.cowbot.postgresql.repository.PlaceRepository;
import ua.zxc.cowbot.postgresql.repository.QueueRepository;
import ua.zxc.cowbot.service.QueueService;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class QueueServiceImpl implements QueueService {

    private final QueueRepository queueRepository;

    private final QueueMapper queueMapper;

    private final PlaceRepository placeRepository;


    public QueueServiceImpl(QueueRepository queueRepository, QueueMapper queueMapper, PlaceRepository placeRepository) {
        this.queueRepository = queueRepository;
        this.queueMapper = queueMapper;
        this.placeRepository = placeRepository;
    }

    @Override
    public QueueDTO getQueueById(Long queueId) {
        log.info("Getting queue by id: {}", queueId);
        QueueEntity queueEntity = queueRepository.findById(queueId)
                .orElse(null);
        if (isNull(queueEntity)) {
            log.warn("Can not to get queue by id: {}", queueId);
            return null;
        }
        log.info("The queue with id {} successfully found", queueId);
        return queueMapper.entityToDto(queueEntity);
    }

    public List<QueueDTO> getAllByChatId(Long chatId) {
        log.info("Getting all queues for chat with id: {}", chatId);
        List<QueueEntity> queueEntitiesByChatId = queueRepository.findAllByChatIdOrderByIdDesc(chatId);
        if (queueEntitiesByChatId.isEmpty()) {
            log.info("Can not to find queues by chat id: {}", chatId);
            return new ArrayList<>();
        }
        log.info("The queues by chat id {} were successfully found", chatId);
        return queueEntitiesByChatId.stream()
                .map(queueMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PageBO<QueueDTO> getAllByChatId(Long chatId, Integer pageNum, Integer pageSize) {
        log.info("Getting all queues for chat with id: {} and pageNum: {} and pageSize: {}", chatId, pageNum, pageSize);
        Page<QueueEntity> queueEntitiesPageByChatId =
                queueRepository.findAllByChatIdOrderByIdDesc(PageRequest.of(pageNum, pageSize), chatId);
        if (!queueEntitiesPageByChatId.hasContent()) {
            log.info("Can not to find queues by chat id: {} and page parameters(pageNum = {} and pageSize = {})",
                    chatId, pageNum, pageSize);
            return new PageBO<>();
        }
        log.info("The queues by chat id {} and page parameters(pageNum = {} and pageSize = {}) were successfully found",
                chatId, pageNum, pageSize);
        List<QueueDTO> queueDTOSByChatId = queueEntitiesPageByChatId.getContent()
                .stream()
                .map(queueMapper::entityToDto)
                .collect(Collectors.toList());
        return new PageBO<>(queueDTOSByChatId, queueEntitiesPageByChatId.getNumber(),
                queueEntitiesPageByChatId.getTotalPages());
    }

    @Override
    public QueueDTO insertQueue(QueueDTO queueDTO) {
        log.info("Inserting queue: {}", queueDTO);
//        if (existsQueueById(queueDTO.getId())) {
//            log.warn("The queue with id {} already exists", queueDTO.getId());
//            return null;
//        }
        if (existsByChatIdAndName(queueDTO)) {
            log.warn("The queue with name {} already exists", queueDTO.getName());
            throw new ObjectAlreadyExistsException("The queue with name" + queueDTO.getName() + " already exists");
        }
        QueueEntity queueEntity = queueMapper.dtoToEntity(queueDTO);
        queueEntity = queueRepository.save(queueEntity);
        log.info("The queue with id {} was successfully saved", queueEntity.getId());
        return queueMapper.entityToDto(queueEntity);
    }

    private boolean existsByChatIdAndName(QueueDTO queue) {
        return queueRepository.existsByChatIdAndName(queue.getChatId(), queue.getName());
    }

    @Override
    public QueueDTO updateQueue(QueueDTO queueDTO) {
        log.info("Updating queue with id: {}", queueDTO.getId());
        if (!existsById(queueDTO.getId())) {
            log.warn("The queue with id {} does not exist", queueDTO.getId());
            return null;
        }
        if (existsByIdNotAndChatIdAndName(queueDTO)) {
            log.warn("The queue with name {} already exists in the chat with id {}, cannot update",
                    queueDTO.getName(), queueDTO.getChatId());
            return null;
        }
        QueueEntity queueEntity = queueMapper.dtoToEntity(queueDTO);
        queueEntity = queueRepository.save(queueEntity);
        log.info("The queue with id {} was successfully updated", queueDTO.getId());
        return queueMapper.entityToDto(queueEntity);
    }

    private boolean existsByIdNotAndChatIdAndName(QueueDTO queue) {
        return queueRepository.existsByIdNotAndChatIdAndName(queue.getId(), queue.getChatId(), queue.getName());
    }

    @Override
    public QueueDTO updateQueueSizeByQueueId(Long queueId, Integer size) {
        log.info("Updating number of places for queue with id: {}", queueId);
        throwIllegalSizeRangeExceptionIfSizeIsNotInAllowRange(size);
        compareMaxTookQueuePlaceAndThrowSizeValueOutOfBoundsExceptionIfSizeIsBigger(queueId, size);
        QueueEntity queueToUpdate = queueRepository.findById(queueId).orElse(null);
        if (isNull(queueToUpdate)) {
            log.warn("Cannot get queue by id: {}", queueId);
            return null;
        }
        queueToUpdate.setSize(size);
        queueToUpdate = queueRepository.save(queueToUpdate);
        log.info("The queue with id {} was successfully updated", queueToUpdate.getId());
        return queueMapper.entityToDto(queueToUpdate);
    }

    private static void throwIllegalSizeRangeExceptionIfSizeIsNotInAllowRange(Integer size) {
        if (isNotInAllowNumberRange(size)) {
            log.warn("The new size must be between 5 and 30");
            throw new IllegalSizeRangeException("The new size of queue must be between 5 and 30");
        }
    }

    private static boolean isNotInAllowNumberRange(int size) {
        return size > 30 || size < 5;
    }

    private void compareMaxTookQueuePlaceAndThrowSizeValueOutOfBoundsExceptionIfSizeIsBigger(Long queueId,
                                                                                             Integer size) {
        PlaceEntity placeWithMaxNumberPlace = getPlaceWithMaxNumberPlace(queueId);
        if (placeWithMaxNumberPlace.getNumber() > size) {
            throw new SizeValueOutOfBoundsException("The new size out of max place number");
        }
    }

    private PlaceEntity getPlaceWithMaxNumberPlace(Long queueId) {
        return placeRepository.findFirstByQueueIdOrderByNumberDesc(queueId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find place for queue with id: " + queueId));
    }

    @Override
    public QueueDTO deleteQueue(Long queueId) {
        log.info("Removing queue by id: {}", queueId);
        try {
            if (!existsById(queueId)) {
                throw new EntityNotFoundException("The queue is removed");
            }
            QueueEntity removedQueue = queueRepository.deleteQueueById(queueId).get(0);
            if (removedQueue == null) {
                throw new RuntimeException("Cannot remove queue with id: " + queueId);
            }
            log.info("The queue with id {} was successfully removed", queueId);
            return queueMapper.entityToDto(removedQueue);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            log.info("The queue with id {} was not removed. Probably it does not exist", queueId, e);
            return null;
        }
    }

    @Override
    public boolean existsById(Long queueId) {
        return queueRepository.existsById(queueId);
    }
}

