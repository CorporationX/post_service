package faang.school.postservice.publisher;


import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.comment.CommentEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Data
@Qualifier("redisTemplate")
public class CommentEventPublisher implements MessagePublisher<CommentEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void publish(CommentEvent message) {
        redisTemplate.convertAndSend(redisProperties.getCommentChannel().getTopic(), message);
    }
}
