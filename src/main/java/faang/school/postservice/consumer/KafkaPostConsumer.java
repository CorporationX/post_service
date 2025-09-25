package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.feed.max-size:500}")
    private int maxFeedSize;

    private static final String FEED_PREFIX = "feed:";

    @KafkaListener(
            topics = "${app.kafka.topics.posts.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(PostCreatedEvent event, Acknowledgment ack) {
        try {
            log.info("Processing PostCreatedEvent: postId={}, authorId={}, followers={}",
                    event.getPostId(), event.getAuthorId(), event.getFollowerIds().size());

            long score = event.getCreatedAt() != null
                    ? event.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                    : System.currentTimeMillis();

            for (Long followerId : event.getFollowerIds()) {
                String key = FEED_PREFIX + followerId;

                redisTemplate.opsForZSet().add(key, event.getPostId(), score);

                Long feedSize = redisTemplate.opsForZSet().zCard(key);
                if (feedSize != null && feedSize > maxFeedSize) {
                    redisTemplate.opsForZSet().removeRange(key, 0, feedSize - maxFeedSize - 1);
                }
            }

            ack.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process PostCreatedEvent: {}", event, e);
        }
    }
}
