package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.LikeEventDto;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.properties.feed.FeedCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class KafkaLikeConsumer extends AbstractKafkaConsumer<LikeEventDto> {

    public KafkaLikeConsumer(ObjectMapper objectMapper,
                             RedisTemplate<String, Object> redisTemplate,
                             FeedCacheProperties feedCacheProperties) {
        super(objectMapper, redisTemplate, feedCacheProperties);
    }

    @KafkaListener(topics = "likes", groupId = "post-service-group")
    public void consume(ConsumerRecord<String, String> record) {
        consumeRecord(record, LikeEventDto.class);
    }

    @Override
    protected void handleEvent(LikeEventDto event) {
        Long postId = event.getPostId();
        String key = "post:" + postId;
        PostRedisDto post = (PostRedisDto) redisTemplate.opsForValue().get(key);

        if (post == null) {
            log.warn("Post {} not found in Redis", postId);
            return;
        }

        Long currentLikes = post.getLikeCount();
        post.setLikeCount(currentLikes + 1);
        redisTemplate.opsForValue().set(key, post, Duration.ofSeconds(feedCacheProperties.getTtlSeconds()));
        log.info("Updated like count to {} for post {}", currentLikes, postId);
    }

    @Override
    protected void logError(Exception e) {
        log.error("Error processing LikeEvent", e);
    }
}
