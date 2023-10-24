package ua.zxc.cowbot.service;

import ua.zxc.cowbot.dto.PlaceDTO;

import java.util.List;

public interface PlaceService {

    PlaceDTO getPlaceById(Long userId, Long queueId);

    PlaceDTO getPlaceByQueueIdAndNumber(Long queueId, Integer number);

    PlaceDTO getPlaceWithMaxNumberPlaceByQueueId(Long queueId);

    List<PlaceDTO> getAllByQueueId(Long queueId);

    PlaceDTO insertPlace(PlaceDTO place);

    PlaceDTO updatePlace(PlaceDTO placeToUpdate);

    void updatePlaces(List<PlaceDTO> places);

    boolean deletePlace(Long userId, Long queueId);

    boolean existsById(Long userId, Long queueId);

    boolean isPlaceFree(Long queueId, Integer number);
}
