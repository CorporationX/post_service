package faang.school.postservice.publisher.comment;

import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublishedCommentEventPublisher implements MessagePublisher<CommentEventDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic publishedCommentEventTopic;

    @Override
    public void publish(CommentEventDto message) {
        redisTemplate.convertAndSend(publishedCommentEventTopic.getTopic(), message);
        log.debug("Message was send {}, in topic - {}", message, publishedCommentEventTopic.getTopic());
    }
}
