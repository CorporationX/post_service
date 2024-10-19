package faang.school.postservice.publisher;

import faang.school.postservice.dto.redis.event.CommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentEventPublisher implements MessagePublisher<CommentEvent> {

    private final RedisTemplate<String, CommentEvent> redisTemplate;
    private final ChannelTopic commentTopic;

    public CommentEventPublisher(RedisTemplate<String, CommentEvent> redisTemplate,
                                 @Qualifier("commentChannel") ChannelTopic commentTopic) {
        this.redisTemplate = redisTemplate;
        this.commentTopic = commentTopic;
    }

    @Retryable(retryFor = {RuntimeException.class}, backoff = @Backoff(delayExpression = "${retryable.delay}"))
    @Override
    public void publish(CommentEvent event) {
        try {
            redisTemplate.convertAndSend(commentTopic.getTopic(), event);
            log.info("Published comment event {}", event);
        } catch (Exception e) {
            log.error("Failed to publish comment event {}", event, e);
            throw new RuntimeException(e);
        }
    }
}
