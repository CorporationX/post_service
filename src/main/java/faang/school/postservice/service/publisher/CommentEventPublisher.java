package faang.school.postservice.service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public class CommentEventPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> template;
    private final ChannelTopic topic;

    @Override
    public void publish(String event) {
        template.convertAndSend(topic.getTopic(), event);
    }
}
