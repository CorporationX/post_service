package faang.school.postservice.consumer;

import faang.school.postservice.events.PostPublishedKafkaEvent;
import faang.school.postservice.model.FeedRedis;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaPostConsumerTest {

    public static final String KAFKA_KEY = "kafka-key";
    public static final String KAFKA_TOPIC = "topic";

    @Mock
    private RedisTemplate<String, FeedRedis> feedRedisTemplate;

    @Mock
    private RedisOperations<String, FeedRedis> operations;

    @Mock
    private ValueOperations<String, FeedRedis> valueOps;

    @InjectMocks
    private KafkaPostConsumer kafkaPostConsumer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaPostConsumer, "topic", KAFKA_TOPIC);
        ReflectionTestUtils.setField(kafkaPostConsumer, "feedTtl", 60 * 60);
        ReflectionTestUtils.setField(kafkaPostConsumer, "feedTtlDuration", Duration.ofHours(1));
        ReflectionTestUtils.setField(kafkaPostConsumer, "maxFeedSizeInRedis", 100);
        ReflectionTestUtils.setField(kafkaPostConsumer, "threadPoolSize", 2);

        kafkaPostConsumer.init();
    }

    @AfterEach
    void destroy() {
        kafkaPostConsumer.destroy();
    }

    @Test
    void processAuthorIdShouldCreateNewFeedForNewUser() throws InterruptedException {
        // Arrange
        Long authorId = 1L;
        long postId = 123L;
        var record = new ConsumerRecord<String, Object>(KAFKA_TOPIC, 0, 0, KAFKA_KEY,
                new PostPublishedKafkaEvent(postId, List.of(authorId)));

        when(feedRedisTemplate.execute(any(SessionCallback.class))).thenAnswer(invocation -> {
            SessionCallback<Boolean> callback = invocation.getArgument(0);

            when(operations.opsForValue()).thenReturn(valueOps);
            when(valueOps.get(authorId.toString())).thenReturn(null);
            when(valueOps.setIfAbsent(eq(authorId.toString()), any(FeedRedis.class), any(Duration.class)))
                    .thenReturn(true);

            return callback.execute(operations);
        });

        // Act
        kafkaPostConsumer.listen(record);
        Thread.sleep(1000);

        // Assert
        verify(feedRedisTemplate).execute(any(SessionCallback.class));
    }

    @Test
    void processAuthorIdShouldUpdateExistingFeed() throws InterruptedException {
        // Arrange
        long authorId = 1L;
        long postId = 456L;
        var existingFeed = FeedRedis.createNew(authorId, 123L);
        var record = new ConsumerRecord<String, Object>(KAFKA_TOPIC, 0, 0, KAFKA_KEY,
                new PostPublishedKafkaEvent(postId, List.of(authorId)));

        when(feedRedisTemplate.execute(any(SessionCallback.class))).thenAnswer(invocation -> {
            SessionCallback<Boolean> callback = invocation.getArgument(0);

            when(operations.opsForValue()).thenReturn(valueOps);
            when(valueOps.get(Long.toString(authorId))).thenReturn(existingFeed);

            operations.multi();
            when(operations.exec()).thenReturn(List.of(true));

            return callback.execute(operations);
        });

        // Act
        kafkaPostConsumer.listen(record);
        Thread.sleep(1000);

        // Assert
        verify(feedRedisTemplate).execute(any(SessionCallback.class));
    }

    @Test
    void processAuthorIdShouldRetryOnTransactionFailure() throws InterruptedException {
        // Arrange
        long authorId = 1L;
        long postId = 456L;
        var existingFeed = FeedRedis.createNew(authorId, 123L);
        var record = new ConsumerRecord<String, Object>(KAFKA_TOPIC, 0, 0, KAFKA_KEY,
                new PostPublishedKafkaEvent(postId, List.of(authorId)));

        when(feedRedisTemplate.execute(any(SessionCallback.class)))
                .thenAnswer(invocation -> {
                    SessionCallback<Boolean> callback = invocation.getArgument(0);

                    when(operations.opsForValue()).thenReturn(valueOps);
                    when(valueOps.get(Long.toString(authorId))).thenReturn(existingFeed);
                    when(operations.exec()).thenReturn(null); // имитируем сбой транзакции

                    return callback.execute(operations);
                })
                .thenAnswer(invocation -> {
                    SessionCallback<Boolean> callback = invocation.getArgument(0);

                    when(operations.opsForValue()).thenReturn(valueOps);
                    when(valueOps.get(Long.toString(authorId))).thenReturn(existingFeed);
                    when(operations.exec()).thenReturn(List.of(true)); // успешная транзакция со второго раза

                    return callback.execute(operations);
                });

        // Act
        kafkaPostConsumer.listen(record);
        Thread.sleep(1000);

        // Assert
        verify(feedRedisTemplate, times(2)).execute(any(SessionCallback.class));
    }

    @Test
    void processAuthorIdShouldHandleException() {
        Long authorId = 1L;
        long postId = 123L;
        var record = new ConsumerRecord<String, Object>(KAFKA_TOPIC, 0, 0, KAFKA_KEY,
                new PostPublishedKafkaEvent(postId, List.of(authorId)));
        var redisExceptionMessage = "Connection failed";
        when(feedRedisTemplate.execute(any(SessionCallback.class)))
                .thenThrow(new RedisConnectionFailureException(redisExceptionMessage));

        assertThrows(CompletionException.class, () -> kafkaPostConsumer.listen(record),
                () -> "Failed to save post %d to user's %d feed: %s".formatted(postId, authorId, redisExceptionMessage));
    }
}