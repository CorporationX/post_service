package faang.school.postservice.publisher;

import org.springframework.data.redis.core.RedisTemplate;

import faang.school.postservice.config.redis.RedisChannelsConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class RedisEventPublisher<E> {
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final RedisChannelsConfig redisChannelsConfig;

    protected void push(String channel, E event) {
        redisTemplate.convertAndSend(channel, event);
    }
}
