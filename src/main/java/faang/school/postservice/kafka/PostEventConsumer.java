package faang.school.postservice.kafka;

import faang.school.postservice.dto.event.PostPublishedEvent;
import faang.school.postservice.repository.redis.NewsFeedRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostEventConsumer {
    private final NewsFeedRedisRepository newsFeedRedisRepository;
    private final RedisLockRegistry redisLockRegistry;

    @Value("${news-feed.max-size}")
    private long maxNewsFeedSize;
    @Value("${spring.data.redis.cache.news-feed.prefix}")
    private String newsFeedPrefix;
    @Value("${spring.data.redis.lock-registry.try-lock-millis}")
    private long tryLockMillis;

    @Async
    @KafkaListener(topics = "${spring.kafka.topic.post.published}", groupId = "${spring.kafka.consumer.group-id.post}")
    public void listener(PostPublishedEvent event, Acknowledgment ack) {
        log.info("Received postPublishedEvent [{}]", event.toString());
        Long postId = event.getPostId();
        for (Long followerId : event.getFollowerIds()) {
            addPostWithLocking(followerId, postId);
        }
        ack.acknowledge();
    }

    private void addPostWithLocking(Long followerId, Long postId) {
        String key = newsFeedPrefix + ":" + followerId;
        log.info("Adding post by id {} to {}", postId, key);
        Lock lock = redisLockRegistry.obtain(key);
        try {
            if (lock.tryLock(tryLockMillis, TimeUnit.MILLISECONDS)) {
                log.info("Key {} locked", key);
                try {
                    addPostToNewsFeed(key, postId);
                } finally {
                    lock.unlock();
                    log.info("Key {} unlocked", key);
                }
            } else {
                log.info("Failed to acquire lock for {}", key);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void addPostToNewsFeed(String key, Long postId) {
        newsFeedRedisRepository.addPostId(key, postId);

        while (newsFeedRedisRepository.getSize(key) > maxNewsFeedSize) {
            log.info("Removing excess post from {}", key);
            newsFeedRedisRepository.removeLastPostId(key);
        }
    }
}
