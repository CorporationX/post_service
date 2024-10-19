package faang.school.postservice.publisher;

import faang.school.postservice.model.event.AlbumCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumCreatedEventPublisher {
    private final ChannelTopic albumEventTopic;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(AlbumCreatedEvent event) {
        redisTemplate.convertAndSend(albumEventTopic.getTopic(), event);
    }
}
