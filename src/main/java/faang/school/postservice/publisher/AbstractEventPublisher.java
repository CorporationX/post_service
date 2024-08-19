package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<E> implements EventPublisher<E>{
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final ChannelTopic topic;


    public void publish(Object message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
