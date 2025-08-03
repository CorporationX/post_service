package faang.school.postservice.kafka.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.redis.cache.service.RedisPostService;
import faang.school.postservice.redis.cache.service.RedisUserService;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventListener<T> {
    protected final ObjectMapper objectMapper;
    protected final PostService postService;
    protected final RedisProperties redisProperties;
    protected final RedisPostService redisPostService;
    protected final RedisUserService redisUserService;

    protected abstract Class<T> getEventClass();

    protected abstract void processEvent(T event);

    public void handle(String message) {
        try {
            T event = objectMapper.readValue(message, getEventClass());
            log.info("Received Kafka event: {}", event);
            processEvent(event);
        } catch (Exception e) {
            log.error("Failed to process Kafka event", e);
        }
    }
}
