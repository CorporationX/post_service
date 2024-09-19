package faang.school.postservice.publisher;

import faang.school.postservice.dto.notification.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic calculations_channelTopic;

    public void publish(CommentEvent commentEvent) {
        redisTemplate.convertAndSend(calculations_channelTopic.getTopic(), commentEvent);
    }
}
