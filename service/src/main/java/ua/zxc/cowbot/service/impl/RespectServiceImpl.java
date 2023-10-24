package ua.zxc.cowbot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.RespectDTO;
import ua.zxc.cowbot.mapper.impl.RespectMapper;
import ua.zxc.cowbot.postgresql.entity.RespectEntity;
import ua.zxc.cowbot.postgresql.entity.embeddedid.UserChatId;
import ua.zxc.cowbot.postgresql.repository.RespectRepository;
import ua.zxc.cowbot.service.RespectService;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class RespectServiceImpl implements RespectService {

    private final RespectRepository respectRepository;

    private final RespectMapper respectMapper;

    public RespectServiceImpl(RespectRepository respectRepository, RespectMapper respectMapper) {
        this.respectRepository = respectRepository;
        this.respectMapper = respectMapper;
    }

    @Override
    public RespectDTO getRespectById(Long userId, Long chatId) {
        log.info("Getting respect by userId: {} and chatId: {}", userId, chatId);
        UserChatId id = createRespectId(userId, chatId);
        RespectEntity respectEntity = respectRepository.findById(id).orElse(null);
        if (isNull(respectEntity)) {
            log.info("Can not to find a respect by userId: {} and chatId: {}", userId, chatId);
            return null;
        }
        log.info("The respect with userId: {} and chatId: {} was successfully found", userId, chatId);
        return respectMapper.entityToDto(respectEntity);
    }

    private UserChatId createRespectId(Long userId, Long chatId) {
        return UserChatId.builder()
                .userId(userId)
                .chatId(chatId)
                .build();
    }

    @Override
    public PageBO<RespectDTO> getAllByChatId(Long chatId, Integer pageNum, Integer pageSize) {
        log.info("Getting all respects for chat with id: {} and pageNum: {} and pageSize: {}", chatId, pageNum, pageSize);
        Page<RespectEntity> respectEntitiesPageByChatId =
                respectRepository.findAllByChatIdOrderByNumberThisMonthDesc(PageRequest.of(pageNum, pageSize), chatId);
        if (!respectEntitiesPageByChatId.hasContent()) {
            log.info("Can not to find respects by chat id: {} and page parameters(pageNum = {} and pageSize = {})",
                    chatId, pageNum, pageSize);
            return new PageBO<>();
        }
        log.info("The respects by chat id {} and page parameters(pageNum = {} and pageSize = {}) were successfully found",
                chatId, pageNum, pageSize);
        List<RespectDTO> respectDTOSByChatId = respectEntitiesPageByChatId.getContent()
                .stream()
                .map(respectMapper::entityToDto)
                .collect(Collectors.toList());
        return new PageBO<>(respectDTOSByChatId, respectEntitiesPageByChatId.getNumber(),
                respectEntitiesPageByChatId.getTotalPages());
    }

    @Override
    public void payRespect(RespectDTO respectDTO) {
        RespectEntity respectEntity = getRespectEntity(createRespectId(respectDTO.getUserId(), respectDTO.getChatId()));
        if (isNull(respectEntity)) {
            increaseRespectNumberAndSaveObject(respectMapper.dtoToEntity(respectDTO));
        } else {
            increaseRespectNumberAndSaveObject(respectEntity);
        }
    }

    private RespectEntity getRespectEntity(UserChatId respectId) {
        return respectRepository.findById(respectId)
                .orElse(null);
    }

    private void increaseRespectNumberAndSaveObject(RespectEntity respect) {
        int increasedRespectNumber = respect.getNumberThisMonth() + 1;
        respect.setNumberThisMonth(increasedRespectNumber);
        respectRepository.save(respect);
    }

    @Override
    public void payDisrespect(RespectDTO respectDTO) {
        RespectEntity respectEntity = getRespectEntity(createRespectId(respectDTO.getUserId(), respectDTO.getChatId()));
        if (isNull(respectEntity)) {
            decreaseRespectNumberAndSaveObject(respectMapper.dtoToEntity(respectDTO));
        } else {
            decreaseRespectNumberAndSaveObject(respectEntity);
        }
    }

    private void decreaseRespectNumberAndSaveObject(RespectEntity respect) {
        int decreasedRespectNumber = respect.getNumberThisMonth() - 1;
        respect.setNumberThisMonth(decreasedRespectNumber);
        respectRepository.save(respect);
    }
}
