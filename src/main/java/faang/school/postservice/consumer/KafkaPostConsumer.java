package faang.school.postservice.consumer;

import faang.school.postservice.events.PostPublishedKafkaEvent;
import faang.school.postservice.exception.RedisOperationException;
import faang.school.postservice.model.FeedRedis;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {

    public final static int WAIT_EXECUTOR_FINISHES_TIMEOUT_MS = 3 * 1000;
    public final static int WAIT_BETWEEN_SAVE_TO_REDIS_ATTEMPTS_MS = 300;

    @Qualifier("feedRedisTemplate")
    private final RedisTemplate<String, FeedRedis> feedRedisTemplate;

    @Value("${news-feed.posts.topic}")
    private String topic;

    @Value("${news-feed.feed.redis-ttl:86400}")
    private long feedTtl;
    private Duration feedTtlDuration;

    @Value("${news-feed.feed.max-redis-feed-size:500}")
    private int maxFeedSizeInRedis;

    @Value("${news-feed.feed.thread-pool-size:-1}")
    private int threadPoolSize;

    private ExecutorService processAuthorIdExecutor;

    @PostConstruct
    public void init() {
        feedTtlDuration = Duration.of(feedTtl, ChronoUnit.SECONDS);

        processAuthorIdExecutor = Executors.newFixedThreadPool(threadPoolSize <= 0
                                                               ? Runtime.getRuntime().availableProcessors() + 1
                                                               : threadPoolSize);
    }

    @PreDestroy
    public void destroy() {
        processAuthorIdExecutor.shutdown();
        try {
            if (!processAuthorIdExecutor.awaitTermination(WAIT_EXECUTOR_FINISHES_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                processAuthorIdExecutor.shutdownNow();
                log.error("processAuthorIdExecutor was stopped forcibly");
            }
        } catch (InterruptedException e) {
            log.error("processAuthorIdExecutor stopped unsuccessfully", e);
        }
    }

    @KafkaListener(topics = "${news-feed.posts.topic}")
    public void listen(ConsumerRecord<String, Object> record) {
        try {
            if (record.value() instanceof PostPublishedKafkaEvent event) {
                processEvent(event);

                log.debug("PostPublishedKafkaEvent is processed for post {}. User ids count - {}", event.getPostId(),
                        event.getAuthorFollowerIds().size());
            } else {
                log.warn("Unknown message type is received from kafka topic {}: {}", topic,
                        record.value() == null ? "null" : record.value().getClass());
            }
        } catch (Exception e) {
            log.error("Processing event from kafka topic {} is failed: {}", topic, e.getMessage(), e);
            throw e;
        }
    }

    private void processEvent(PostPublishedKafkaEvent postPublishedKafkaEvent) {
        var futures = postPublishedKafkaEvent.getAuthorFollowerIds()
                .stream()
                .map(authorId -> CompletableFuture.runAsync(
                        () -> processAuthorId(authorId, postPublishedKafkaEvent.getPostId()
                        ), processAuthorIdExecutor))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
    }

    private void processAuthorId(Long authorId, long postId) {
        var redisKey = authorId.toString();
        while (true) {
            try {
                var result = feedRedisTemplate.execute(new SessionCallback<Boolean>() {
                    public <K, V> Boolean execute(@NonNull RedisOperations<K, V> operations) {
                        var typedOperations = (RedisOperations<String, FeedRedis>) operations;

                        typedOperations.watch(redisKey);
                        var currentRedisValue = (FeedRedis) typedOperations.opsForValue().get(redisKey);

                        if (currentRedisValue == null) {
                            typedOperations.unwatch();

                            var newRedisValue = FeedRedis.createNew(authorId, postId);

                            if (Boolean.TRUE.equals(
                                    typedOperations.opsForValue()
                                            .setIfAbsent(redisKey, newRedisValue, feedTtlDuration))) {
                                return Boolean.TRUE;
                            }

                            log.debug(
                                    "Failed to add new feed for user {} to redis: another thread has added it. Try again",
                                    authorId);

                            return Boolean.FALSE;
                        }
                        currentRedisValue.addPostId(postId, maxFeedSizeInRedis);
                        currentRedisValue.updateVersion();

                        typedOperations.multi();
                        typedOperations.opsForValue().set(redisKey, currentRedisValue);
                        typedOperations.expire(redisKey, feedTtlDuration);

                        var execResult = operations.exec();

                        if (execResult != null && !execResult.isEmpty()) {
                            return Boolean.TRUE;
                        }

                        log.debug("Failed to update for user {} to redis: another thread has updated it. Try again",
                                authorId);

                        return Boolean.FALSE;
                    }
                });

                if (result) {
                    return;
                }

                Thread.sleep(WAIT_BETWEEN_SAVE_TO_REDIS_ATTEMPTS_MS);
            } catch (Exception e) {
                var message = "Failed to save post %d to user's %d feed: %s".formatted(postId, authorId,
                        e.getMessage());
                log.error(message, e);

                throw new RedisOperationException(message, e);
            }
        }
    }
}
