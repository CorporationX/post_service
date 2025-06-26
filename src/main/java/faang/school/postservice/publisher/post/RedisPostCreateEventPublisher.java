package faang.school.postservice.publisher.post;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import faang.school.postservice.config.redis.RedisChannelsConfig;
import faang.school.postservice.dto.event.PostCreateEventDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisPostCreateEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisChannelsConfig redisChannelsConfig;

    public void push(PostCreateEventDto postCreateEventDto) {
        redisTemplate.convertAndSend(redisChannelsConfig.getPostCreated(), postCreateEventDto);
    }

}
