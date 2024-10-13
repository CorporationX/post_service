package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisherImpl implements MessagePublisher<LikeEvent> {

    private RedisTemplate<String, String> redisTemplate;

    @Value("{spring.data.redis.channel.like}")
    private String topic;

    @Override
    public void publish(LikeEvent event) {
        redisTemplate.convertAndSend(topic, event);
    }
}
