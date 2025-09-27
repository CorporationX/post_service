package faang.school.postservice.newsfeed.kafka;

import faang.school.postservice.config.redis.dto.CommentEvent;
import faang.school.postservice.newsfeed.redis.RedisCommentCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventListener implements EventConsumerCache<CommentEvent> {

    private final RedisCommentCacheService redisCache;

    @Override
    @KafkaListener(
            topics = "${kafka.topics.comment-event}",
            groupId = "${kafka.consumer.group-id}"
    )
    public void consume(CommentEvent event, Acknowledgment ack) {
        try {
            redisCache.saveComment(event);
            ack.acknowledge();
            log.info("comment {} cached for post {}", event.commentId(), event.postId());
        } catch (Exception e) {
            log.error("comment {} cache error", event.commentId(), e);
        }
    }
}