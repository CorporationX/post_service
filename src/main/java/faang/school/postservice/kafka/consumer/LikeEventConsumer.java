package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.event.like.LikeAddedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LikeEventConsumer {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.cache.post.prefix}")
    private String postPrefix;
    @Value("${spring.data.redis.cache.post.field.likes}")
    private String likesField;

    @Async
    @KafkaListener(topics = "${spring.kafka.topic.like.added}", groupId = "${spring.kafka.consumer.group-id}")
    public void listener(LikeAddedEvent event, Acknowledgment ack) {
        log.info("Received likeAddedEvent [{}]", event.toString());
        String key = postPrefix + event.getPostId();
        boolean isInCache = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        if (isInCache) {
            log.info("Adding like to post by id {}", event.getPostId());
            redisTemplate.opsForHash().increment(key, likesField, 1);
        } else {
            log.info("Post by id {} not found in cache", event.getPostId());
        }
        ack.acknowledge();
    }
}
