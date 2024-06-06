package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostViewEventPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void publish(PostViewEvent postViewEvent) {
        redisTemplate.convertAndSend(topic.getTopic(), postViewEvent);
    }
}
