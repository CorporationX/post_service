package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisherImpl implements MessagePublisher<LikeEvent> {

    private final RedisTemplate<String, LikeEvent> redisTemplate;

    private final ChannelTopic likeEventTopic;

    @Override
    public void publish(LikeEvent event) {
        redisTemplate.convertAndSend(likeEventTopic.getTopic(), event);
    }
}
