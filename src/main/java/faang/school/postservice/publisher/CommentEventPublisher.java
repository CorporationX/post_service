package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventPublisher implements MessagePublisher<CommentEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topicComment;


    @Override
    public void publishMessage(CommentEvent event) {
        try {
            redisTemplate.convertAndSend(topicComment.getTopic(), event);
            log.info("Published event: {}", event);
        } catch (RuntimeException e) {
            log.error("Failed to publish event: {}", e.getMessage(), e);
        }

    }
}
