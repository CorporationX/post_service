package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostPublishDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final RedisTemplate<String, String> redisTemplate;
    private final PostService postService;
    private final ObjectMapper objectMapper;

    @Value("${feed.max-size}")
    private int maxFeedSize;

    private static final String COUNTER_KEY = "feed:counter";

    @KafkaListener(topics = "${kafka.topic.post.name}", groupId = "feed-consumer-group")
    public void consumePostEvent(String message, Acknowledgment acknowledgment) {
        try {
            PostPublishDto event = objectMapper.readValue(message, PostPublishDto.class);
            log.info("Received Kafka event for postId: {}", event.getPostId());

            Post post = postService.getPostEntity(event.getPostId());
            if (post == null || !post.isPublished()) {
                log.warn("Post with id {} not found or not published", event.getPostId());
                acknowledgment.acknowledge();
                return;
            }

            processSubscribersFeed(event);

            acknowledgment.acknowledge();
            log.info("Successfully processed event for postId: {}", event.getPostId());
        } catch (Exception e) {
            log.error("Error processing Kafka event: {}", message, e);
            throw new RuntimeException("Failed to process Kafka event", e);
        }
    }

    private void processSubscribersFeed(PostPublishDto event) {
        Long score = redisTemplate.opsForValue().increment(COUNTER_KEY);
        if (score == null) {
            log.error("Failed to increment counter for postId: {}", event.getPostId());
            return;
        }

        for (Long subscriberId : event.getSubscribersIds()) {
            try {
                String feedKey = "feed:" + subscriberId;
                redisTemplate.executePipelined((RedisCallback<?>) (connection) -> {
                    redisTemplate.opsForZSet().add(feedKey, String.valueOf(event.getPostId()), score);

                    Long feedSize = redisTemplate.opsForZSet().zCard(feedKey);
                    if (feedSize != null && feedSize > maxFeedSize) {
                        redisTemplate.opsForZSet().removeRange(feedKey, 0, feedSize - maxFeedSize - 1);
                    }
                    return null;
                });
                log.debug("Updated feed for subscriberId: {}, postId: {}", subscriberId, event.getPostId());
            } catch (Exception e) {
                log.error("Failed to update feed for subscriberId: {}, postId: {}", subscriberId, event.getPostId(), e);
            }
        }
    }
}

