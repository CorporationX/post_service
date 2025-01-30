package faang.school.postservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {
    private final StringRedisTemplate redisTemplate;
    private static final String POST_VIEWS_KEY_PREFIX = "post:views";

    @KafkaListener(topics = "post-views", groupId = "post-views-consumer-group")
    public void consumePostViewEvent(String postId) {
        try {
            log.info("Received post view event for post with ID: {}", postId);

            String redisKey = POST_VIEWS_KEY_PREFIX + ":" + postId;
            Long updatedCount = redisTemplate.opsForValue().increment(redisKey);

            log.info("Updated view count for post with ID: {} to: {}", postId, updatedCount);
        } catch (Exception e) {
            log.error("Error processing post view event for post with ID: {}", postId, e);
        }
    }
}
