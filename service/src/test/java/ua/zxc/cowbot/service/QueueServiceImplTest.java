package ua.zxc.cowbot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.zxc.cowbot.ServiceSpringBootTest;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.helper.QueueInitializr;
import ua.zxc.cowbot.postgresql.entity.QueueEntity;
import ua.zxc.cowbot.postgresql.repository.QueueRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class QueueServiceImplTest extends ServiceSpringBootTest {

    @MockBean
    private QueueRepository queueRepository;
//    @MockBean
//    private ChatRepository chatRepository;

    @Autowired
    private QueueService queueService;

    private static final QueueDTO QUEUE_DTO = QueueInitializr.createDTO();

    private static final QueueEntity QUEUE_ENTITY = QueueInitializr.createEntity();

    @Test
    public void getQueueByIdWithExistingIdShouldReturnQueue() {
        when(queueRepository.findById(anyLong())).thenReturn(Optional.of(QUEUE_ENTITY));

        QueueDTO actualQueue = queueService.getQueueById(1L);

        assertEquals(QUEUE_ENTITY.getName(), actualQueue.getName());
    }

    @Test
    public void getQueueByIdWithNotExistingIdShouldReturnNull() {
        when(queueRepository.findById(anyLong())).thenReturn(Optional.empty());

        QueueDTO actualQueue = queueService.getQueueById(1L);

        assertNull(actualQueue);
    }

//    @Test
//    public void getAllByChatIddWithExistingQueuesByChatIdShouldReturnChatsSet() {
//        when(queueRepository.findAllByChatIdOrderByIdDesc(anyLong()))
//                .thenReturn(Collections.singletonList(QUEUE_ENTITY));
//
//        List<QueueDTO> actualQueuesByChatId = queueService.getAllByChatId(1L);
//
//        assertNotNull(actualQueuesByChatId);
//        assertTrue(actualQueuesByChatId.contains(QUEUE_DTO));
//    }

    @Test
    public void getAllByChatIddWithNotExistingQueuesByChatIdShouldReturnEmptySet() {
        when(queueRepository.findAllByChatIdOrderByIdDesc(anyLong())).thenReturn(Collections.emptyList());

        List<QueueDTO> actualQueuesByChatId = queueService.getAllByChatId(1L);

        assertNotNull(actualQueuesByChatId);
        assertTrue(actualQueuesByChatId.isEmpty());
    }

//    @Test
//    public void insertQueueWithNotExistingQueueIdShouldReturnNewQueue() {
//        when(queueRepository.existsById(anyLong())).thenReturn(false);
//        when(queueRepository.save(any(QueueEntity.class))).thenReturn(QUEUE_ENTITY);
//
//        QueueDTO actualQueue = queueService.insertQueue(QUEUE_DTO);
//
//        assertEquals(QUEUE_ENTITY.getName(), actualQueue.getName());
//    }

//    @Test
//    public void updateQueueWithExistingQueueIdShouldBeOk() {
//        when(queueRepository.existsById(anyLong())).thenReturn(true);
//        when(queueRepository.save(any(QueueEntity.class))).thenReturn(QUEUE_ENTITY);
//
//        QueueDTO actualQueue = queueService.updateQueue(QUEUE_DTO);
//
//        assertEquals(QUEUE_ENTITY.getName(), actualQueue.getName());
//    }

    @Test
    public void updateQueueWithNotExistingQueueIdShouldReturnNull() {
        when(queueRepository.existsById(anyLong())).thenReturn(false);

        QueueDTO actualQueue = queueService.updateQueue(QUEUE_DTO);

        assertNull(actualQueue);
    }

//    @Test
//    public void deleteQueueWithExistingIdShouldReturnTrue() {
//        QueueDTO removedQueue = queueService.deleteQueue(3L);
//
//        assertNotNull(removedQueue);
//    }

    @Test
    public void deleteQueueWithNotExistingIdShouldReturnFalse() {
        QueueDTO removedQueue = queueService.deleteQueue(333L);

        assertNull(removedQueue);
    }
}
