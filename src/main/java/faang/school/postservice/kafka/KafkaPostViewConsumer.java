package faang.school.postservice.kafka;

import faang.school.postservice.dto.kafka.PostViewEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String POST_VIEWS_KEY_PREFIX = "post:views:";
    private static final String POST_VIEWS_LOCK_PREFIX = "post:views:lock:";

    @KafkaListener(topics = "${spring.kafka.topic.post-views}", groupId = "post-view-consumer-group")
    public void consume(@Payload @Valid PostViewEvent event) {
        log.info("Received post view event for postId: {}", event.getPostId());

        String key = POST_VIEWS_KEY_PREFIX + event.getPostId();
        String lockKey = POST_VIEWS_LOCK_PREFIX + event.getPostId();

        try {
            while (Boolean.FALSE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 1, TimeUnit.SECONDS))) {
                Thread.sleep(50);
            }

            redisTemplate.opsForValue().increment(key, 1);

            if (redisTemplate.getExpire(key) == -1) {
                redisTemplate.expire(key, 30, TimeUnit.DAYS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while waiting for Redis lock", e);
        } finally {

            redisTemplate.delete(lockKey);
        }
    }
}