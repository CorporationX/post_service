package faang.school.postservice.service.publisher;

import faang.school.postservice.dto.event.CommentEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic commentChannel;

    public void publish(CommentEventDto event) {
        redisTemplate.convertAndSend(commentChannel.getTopic(), event);
    }
}
