package faang.school.postservice.publishers;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postCommentChannel;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(postCommentChannel.getTopic(), message);
    }
}
