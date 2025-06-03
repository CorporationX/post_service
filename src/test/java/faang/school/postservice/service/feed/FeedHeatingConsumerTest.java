package faang.school.postservice.service.feed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedHeatingConsumerTest {
    @Mock
    private FeedCacheService feedCacheService;

    @Mock
    private ThreadPoolExecutor processingExecutor;

    @Mock
    private BlockingQueue<Runnable> queue;

    @InjectMocks
    private FeedHeatingConsumer feedHeatingConsumer;

    @BeforeEach
    void setUp() {
        when(processingExecutor.getQueue()).thenReturn(queue);
    }

    @Test
    void testConsume_NormalFlow() {
        Long userId = 123L;
        when(queue.size()).thenReturn(0);

        feedHeatingConsumer.consume(userId);

        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(processingExecutor).submit(taskCaptor.capture());

        taskCaptor.getValue().run();
        verify(feedCacheService).warmUpCacheForUser(userId);
    }

    @Test
    void testConsume_QueueFull() {
        Long userId = 123L;
        when(queue.size()).thenReturn(10001);

        feedHeatingConsumer.consume(userId);

        verify(processingExecutor, never()).submit(any(Runnable.class));
        verifyNoInteractions(feedCacheService);
    }

    @Test
    void testConsume_ServiceException() {
        Long userId = 123L;
        when(queue.size()).thenReturn(0);
        doThrow(new RuntimeException("Service error")).when(feedCacheService).warmUpCacheForUser(userId);

        feedHeatingConsumer.consume(userId);

        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(processingExecutor).submit(taskCaptor.capture());
        taskCaptor.getValue().run();

        verify(feedCacheService).warmUpCacheForUser(userId);
    }

    @Test
    void testConsume_DatabaseException() {
        Long userId = 123L;
        when(queue.size()).thenReturn(0);
        doThrow(new DataAccessException("DB error") {
        }).when(feedCacheService).warmUpCacheForUser(userId);

        feedHeatingConsumer.consume(userId);

        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(processingExecutor).submit(taskCaptor.capture());
        taskCaptor.getValue().run();

        verify(feedCacheService).warmUpCacheForUser(userId);
    }
}
