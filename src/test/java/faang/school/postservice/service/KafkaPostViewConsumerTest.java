package faang.school.postservice.service;

import faang.school.postservice.dto.kafka.PostViewEvent;
import faang.school.postservice.kafka.KafkaPostViewConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaPostViewConsumerTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private KafkaPostViewConsumer kafkaPostViewConsumer;

    private final long postId = 123L;
    private PostViewEvent event;

    @BeforeEach
    void setUp() {
        event = PostViewEvent.builder()
                .postId(postId)
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testConsumeSuccess() {
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
        when(valueOperations.increment(anyString(), anyLong())).thenReturn(1L);
        when(redisTemplate.getExpire(anyString())).thenReturn(-1L);

        kafkaPostViewConsumer.consume(event);

        verify(valueOperations).setIfAbsent(
                "post:views:lock:123", "locked", 1, TimeUnit.SECONDS);
        verify(valueOperations).increment("post:views:123", 1);
        verify(redisTemplate).expire("post:views:123", 30, TimeUnit.DAYS);
        verify(redisTemplate).delete("post:views:lock:123");
    }

    @Test
    void testConsumeRetryLock() throws InterruptedException {
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(false)
                .thenReturn(true);
        when(valueOperations.increment(anyString(), anyLong())).thenReturn(1L);
        when(redisTemplate.getExpire(anyString())).thenReturn(-1L);

        kafkaPostViewConsumer.consume(event);

        verify(valueOperations, times(2)).setIfAbsent(
                "post:views:lock:123", "locked", 1, TimeUnit.SECONDS);
        verify(valueOperations).increment("post:views:123", 1);
        verify(redisTemplate).delete("post:views:lock:123");
    }

    @Test
    void testConsumeInterruptedException() {
        doAnswer(invocation -> {
            Thread.sleep(50);
            Thread.currentThread().interrupt();
            throw new InterruptedException("Test interrupt");
        }).when(valueOperations).setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        kafkaPostViewConsumer.consume(event);

        assertTrue(Thread.interrupted(), "Thread interrupt flag should be set");
        verify(redisTemplate).delete("post:views:lock:123");
    }

    @Test
    void testConsumeExpireNotSetWhenAlreadyExists() {
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
        when(valueOperations.increment(anyString(), anyLong())).thenReturn(1L);
        when(redisTemplate.getExpire(anyString())).thenReturn(100L);

        kafkaPostViewConsumer.consume(event);

        verify(valueOperations).increment("post:views:123", 1);
        verify(redisTemplate, never()).expire(anyString(), anyLong(), any(TimeUnit.class));
    }
}