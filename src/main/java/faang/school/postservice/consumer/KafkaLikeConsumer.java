package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {

    private final RedisTemplate<String, Object> redisTemplate;

    @KafkaListener(topics = "likesTopic",groupId ="post-group",containerFactory = "kafkaListenerContainerFactoryJson")
    public void consumeLike(LikeEvent event) {
        log.info("Event received: {}", event);
        String key = "posts:" + event.getPostId();

        Boolean exists = redisTemplate.hasKey(key);
        log.info("exists: " + exists);
        if (exists == null || !exists) {
            log.warn("Post {} not found in Redis", event.getPostId());
            return;
        }
        log.info("Post {} found in Redis", event.getPostId());

        Long newLikeCount = redisTemplate.opsForHash().increment(key, "likeCount", 1);

        log.info("Post {} like count updated to {}", event.getPostId(), newLikeCount);

    }
}

