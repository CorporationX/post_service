package faang.school.postservice.publisher.like;

import faang.school.postservice.config.redis.RedisConfiguration;
import faang.school.postservice.dto.like.LikeEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher {

    private final RedisTemplate<String, LikeEventDto> redisTemplate;
    private final ChannelTopic topic;

    public void publish(LikeEventDto likeEventDto) {
        redisTemplate.convertAndSend(topic.getTopic(), likeEventDto);
    }
}
