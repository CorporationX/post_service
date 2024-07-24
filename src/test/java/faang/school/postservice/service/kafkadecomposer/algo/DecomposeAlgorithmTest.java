package faang.school.postservice.service.kafkadecomposer.algo;

import faang.school.postservice.dto.user.UserFeedDto;
import faang.school.postservice.publisher.kafka.KafkaPostPublisher;
import faang.school.postservice.threadpool.ThreadPoolForNewsFeedAlgo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DecomposeAlgorithmTest {

    @Mock
    private KafkaPostPublisher kafkaPostPublisher;

    @Mock
    private ThreadPoolForNewsFeedAlgo threadPoolForNewsFeedAlgo;

    @InjectMocks
    private DecomposeAlgorithmImpl decomposeAlgorithm;

    private ExecutorService executorService;

    @BeforeEach
    public void setUp() {
        executorService = Executors.newFixedThreadPool(10);
        when(threadPoolForNewsFeedAlgo.newsFeedAloPool()).thenReturn(executorService);
    }

    @Test
    public void testCorrectWorkModerateComment() {
        decomposeAlgorithm.startAlgo(List.of(new UserFeedDto(1L), new UserFeedDto(2L)), 20L, 10);

        verify(kafkaPostPublisher, times(10)).sendMessage(anyList(), eq(20L));
        executorService.shutdownNow();
    }

    @Test
    public void testBatchSizeGreaterThanListSize() {
        decomposeAlgorithm.startAlgo(List.of(new UserFeedDto(1L), new UserFeedDto(2L)), 20L, 5);

        verify(kafkaPostPublisher, times(5)).sendMessage(anyList(), eq(20L));
        executorService.shutdownNow();
    }

    @Test
    public void testBatchSizeOne() {
        List<UserFeedDto> userDtoList = List.of(new UserFeedDto(1L), new UserFeedDto(2L), new UserFeedDto(3L));

        decomposeAlgorithm.startAlgo(userDtoList, 20L, 1);

        verify(kafkaPostPublisher, times(1)).sendMessage(anyList(), eq(20L));
        executorService.shutdownNow();
    }

    @Test
    public void testInterruptedExceptionHandling() {
        ExecutorService mockExecutor = mock(ExecutorService.class);
        lenient().when(mockExecutor.submit(any(Runnable.class))).thenAnswer(invocation -> {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new InterruptedException("Test exception"));
            return future;
        });
        lenient().when(threadPoolForNewsFeedAlgo.newsFeedAloPool()).thenReturn(mockExecutor);

        decomposeAlgorithm.startAlgo(List.of(new UserFeedDto(1L), new UserFeedDto(2L)), 20L, 2);

        verify(kafkaPostPublisher, never()).sendMessage(anyList(), eq(20L));
    }

    @Test
    public void testTimeoutExceptionHandling() {
        List<UserFeedDto> userDtoList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            userDtoList.add(new UserFeedDto());
        }

        ExecutorService mockExecutor = mock(ExecutorService.class);
        lenient().when(mockExecutor.submit(any(Runnable.class))).thenAnswer(invocation -> {
            CompletableFuture<Void> future = new CompletableFuture<>();
            TimeUnit.MILLISECONDS.sleep(200);
            return future;
        });
        lenient().when(threadPoolForNewsFeedAlgo.newsFeedAloPool()).thenReturn(mockExecutor);

        decomposeAlgorithm.startAlgo(userDtoList, 20L, 10);

        verify(kafkaPostPublisher, atMost(10)).sendMessage(anyList(), eq(20L));
    }
}
