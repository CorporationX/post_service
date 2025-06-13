package faang.school.postservice.service.publisher;

import faang.school.postservice.dto.event.PostViewEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostViewEventPublisher {

    private final RedisTemplate<String, PostViewEventDto> redisTemplate;
    private final ChannelTopic postViewTopic;

    public void publish(PostViewEventDto event) {
        redisTemplate.convertAndSend(postViewTopic.getTopic(), event);
    }
}
