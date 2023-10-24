package ua.zxc.cowbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.zxc.cowbot.dto.PlaceDTO;
import ua.zxc.cowbot.postgresql.entity.PlaceEntity;
import ua.zxc.cowbot.postgresql.entity.embeddedid.UserQueueId;
import ua.zxc.cowbot.postgresql.repository.PlaceRepository;
import ua.zxc.cowbot.service.PlaceService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    private final PlaceMapper placeMapper;

    public PlaceServiceImpl(PlaceRepository placeRepository, PlaceMapper placeMapper) {
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
    }

    @Override
    public PlaceDTO getPlaceById(Long userId, Long queueId) {
        log.info("Getting place by userId: {} and queueId: {}", userId, queueId);
        try {
            PlaceEntity placeById = getPlaceById(createPlaceId(userId, queueId));
            log.info("The place with userId: {} and queueId: {} was successfully found", userId, queueId);
            return placeMapper.entityToDto(placeById);
        } catch (RuntimeException e) {
            log.warn("Cannot find a place", e);
            return null;
        }
    }

    private PlaceEntity getPlaceById(UserQueueId id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot get place: [id = '" + id + "']"));
    }

    private UserQueueId createPlaceId(Long userId, Long queueId) {
        return UserQueueId.builder()
                .userId(userId)
                .queueId(queueId)
                .build();
    }

    @Override
    public PlaceDTO getPlaceByQueueIdAndNumber(Long queueId, Integer number) {
        return null;
    }

    @Override
    public PlaceDTO getPlaceWithMaxNumberPlaceByQueueId(Long queueId) {
        log.info("Getting place with max number place by and queueId: {}", queueId);
        PlaceEntity placeEntity = placeRepository.findFirstByQueueIdOrderByNumberDesc(queueId).orElse(null);
        if (isNull(placeEntity)) {
            log.info("Can not to find a place with max number place by and queueId: {}", queueId);
            return null;
        }
        log.info("The place with max number place by and queueId: {} was successfully found", queueId);
        return placeMapper.entityToDto(placeEntity);
    }

    @Override
    public List<PlaceDTO> getAllByQueueId(Long queueId) {
        log.info("Getting places by queue id: {}", queueId);
        List<PlaceEntity> placeEntitiesByUserId = placeRepository.findAllByQueueIdOrderByNumberAsc(queueId);
        if (placeEntitiesByUserId.isEmpty()) {
            log.warn("Can not to find places by queue id: {}", queueId);
            return new ArrayList<>();
        }
        log.info("The places by queue id {} were successfully found", queueId);
        return placeEntitiesByUserId.stream()
                .map(placeMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlaceDTO insertPlace(PlaceDTO place) {
        log.info("Inserting a place: {}", place);
        if (existsById(place.getUserId(), place.getQueueId())) {
            log.info("The lesson already exists: [userId = '{}' and queueId '{}']",
                    place.getUserId(), place.getQueueId());
            return null;
        }
        PlaceEntity placeEntity = placeMapper.dtoToEntity(place);
        placeEntity = placeRepository.save(placeEntity);
        log.info("The place successfully inserted: [userId = '{}' and queueId '{}']",
                place.getUser(), place.getQueueId());
        return placeMapper.entityToDto(placeEntity);
    }

    @Override
    public PlaceDTO updatePlace(PlaceDTO placeDTO) {
        log.info("Updating a place with user id: {} and queue id: {} and number: {}",
                placeDTO.getUserId(), placeDTO.getQueueId(), placeDTO.getNumber());
        if (!existsById(placeDTO.getUserId(), placeDTO.getQueueId())) {
            log.info("The place with user id: {} and queue id: {} does not exist",
                    placeDTO.getUserId(), placeDTO.getQueueId());
            return null;
        }
        PlaceEntity placeToUpdate = placeMapper.dtoToEntity(placeDTO);
        placeToUpdate = placeRepository.save(placeToUpdate);
        log.info("The place with user id: {} and queue id: {} and number: {} successfully updated",
                placeDTO.getUserId(), placeDTO.getQueueId(), placeDTO.getNumber());
        return placeMapper.entityToDto(placeToUpdate);
    }

    @Override
    @Transactional
    public void updatePlaces(List<PlaceDTO> places) {
        log.info("Updating places");
        List<PlaceEntity> placesToUpdate = new ArrayList<>();
        for (PlaceDTO placeDTO : places) {
            if (!existsById(placeDTO.getUserId(), placeDTO.getQueueId())) {
                log.info("The place with user id: {} and queue id: {} does not exist",
                        placeDTO.getUserId(), placeDTO.getQueueId());
                return;
            }
            placesToUpdate.add(updatePlaceNumber(placeDTO));
        }
        List<PlaceEntity> updatedPlaces = (List<PlaceEntity>) placeRepository.saveAll(placesToUpdate);
        log.info("The places successfully updated");
        placeMapper.entitiesToDtos(updatedPlaces);
    }

    private PlaceEntity updatePlaceNumber(PlaceDTO placeDTO) {
        PlaceEntity placeById = getPlaceById(createPlaceId(placeDTO.getUserId(), placeDTO.getQueueId()));
        placeById.setNumber(placeDTO.getNumber());
        return placeById;
    }

    @Override
    public boolean deletePlace(Long userId, Long queueId) {
        log.info("Removing place by user id: {} and queue id: {}", userId, queueId);
        try {
            placeRepository.deleteById(createPlaceId(userId, queueId));
            log.info("The place with user id {} and queue id {} was successfully removed", userId, queueId);
            return true;
        } catch (RuntimeException e) {
            log.info("The place with user id {} and queue id {} was not removed. Probably it does not exist",
                    userId, queueId, e);
            return false;
        }
    }

    @Override
    public boolean existsById(Long userId, Long queueId) {
        return placeRepository.existsById(createPlaceId(userId, queueId));
    }

    @Override
    public boolean isPlaceFree(Long queueId, Integer number) {
        return !placeRepository.existsByQueueIdAndNumber(queueId, number);
    }
}
