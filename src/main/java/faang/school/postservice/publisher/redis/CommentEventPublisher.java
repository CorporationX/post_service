package faang.school.postservice.publisher.redis;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.event.CommentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventPublisher implements MessagePublisher<CommentEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topicComment;

    @Value("${spring.data.redis.channels.comment_event_channel.name}")
    private String channel;


    @Override
    public void publishMessage(CommentEvent event) {
        try {
            redisTemplate.convertAndSend(topicComment.getTopic(), event);
            log.info("Published event: {}", event);
        } catch (RuntimeException e) {
            log.error("Failed to publish event: {}", e.getMessage(), e);
        }

    }

    public void publish(CommentEventDto commentEventDto) {
        log.info("Publishing comment event notification, authorId: {}, receiverId: {}, commentId: {}, postId: {}",
                commentEventDto.authorId(), commentEventDto.receivedId(),
                commentEventDto.commentId(), commentEventDto.postId());
        try {
            redisTemplate.convertAndSend(channel, commentEventDto);
        } catch (RuntimeException e) {
            log.error("Conversion or sending message failed with error: ", e);
        }
        log.info("Comment event notification sent");
    }
}
