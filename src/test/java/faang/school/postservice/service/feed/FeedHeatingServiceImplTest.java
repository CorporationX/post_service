package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedHeatingServiceImplTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private FeedHeatingProducer feedHeatingProducer;

    @Mock
    private ThreadPoolExecutor taskSplitterExecutor;

    @InjectMocks
    private FeedHeatingServiceImpl feedHeatingService;

    private static final int MAX_QUEUE_SIZE = 10000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedHeatingService, "batchSize", 2);
        ReflectionTestUtils.setField(feedHeatingService, "maxPages", 5);
    }

    @Test
    void testSubmitHeatingJob_QueueFull() {
        BlockingQueue<Runnable> queue = mock(BlockingQueue.class);
        when(queue.size()).thenReturn(MAX_QUEUE_SIZE + 1);
        when(taskSplitterExecutor.getQueue()).thenReturn(queue);

        boolean result = feedHeatingService.submitHeatingJob();
        assertFalse(result, "When the queue is full, the method should return false");

        verify(taskSplitterExecutor, never()).submit(any(Runnable.class));
        verifyNoInteractions(userServiceClient);
        verifyNoInteractions(feedHeatingProducer);
    }

    @Test
    void testSubmitHeatingJob_NoUsers() {
        BlockingQueue<Runnable> queue = mock(BlockingQueue.class);
        when(queue.size()).thenReturn(0);
        when(taskSplitterExecutor.getQueue()).thenReturn(queue);

        when(userServiceClient.getUserIdsBatch(0, 2)).thenReturn(Collections.emptyList());

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(taskSplitterExecutor).submit(any(Runnable.class));

        boolean result = feedHeatingService.submitHeatingJob();
        assertTrue(result, "When the queue is not full, the method should return true");

        verify(userServiceClient).getUserIdsBatch(0, 2);
        verifyNoInteractions(feedHeatingProducer);
    }

    @Test
    void testSubmitHeatingJob_MultiplePages() {
        BlockingQueue<Runnable> queue = mock(BlockingQueue.class);
        when(queue.size()).thenReturn(0);
        when(taskSplitterExecutor.getQueue()).thenReturn(queue);

        when(userServiceClient.getUserIdsBatch(0, 2)).thenReturn(Arrays.asList(1L, 2L));
        when(userServiceClient.getUserIdsBatch(1, 2)).thenReturn(Collections.singletonList(3L));

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(taskSplitterExecutor).submit(any(Runnable.class));

        boolean result = feedHeatingService.submitHeatingJob();
        assertTrue(result);

        verify(userServiceClient).getUserIdsBatch(0, 2);
        verify(userServiceClient).getUserIdsBatch(1, 2);

        verify(feedHeatingProducer).sendHeatingTask(1L);
        verify(feedHeatingProducer).sendHeatingTask(2L);
        verify(feedHeatingProducer).sendHeatingTask(3L);
    }

    @Test
    void testSubmitHeatingJob_ServiceThrowsException() {
        BlockingQueue<Runnable> queue = mock(BlockingQueue.class);
        when(queue.size()).thenReturn(0);
        when(taskSplitterExecutor.getQueue()).thenReturn(queue);

        when(userServiceClient.getUserIdsBatch(0, 2)).thenThrow(mock(FeignException.class));

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return CompletableFuture.completedFuture(null);
        }).when(taskSplitterExecutor).submit(any(Runnable.class));

        boolean result = feedHeatingService.submitHeatingJob();
        assertTrue(result);

        verify(userServiceClient).getUserIdsBatch(0, 2);
        verifyNoInteractions(feedHeatingProducer);
    }
}
