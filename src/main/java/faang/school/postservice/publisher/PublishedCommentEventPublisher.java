package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublishedCommentEventPublisher implements MessagePublisher<CommentEventDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic newCommentEventTopic;

    @Override
    public void publish(CommentEventDto message) {
        redisTemplate.convertAndSend(newCommentEventTopic.getTopic(), message);
    }
}
