package faang.school.postservice.publisher;

import faang.school.postservice.mapper.post.JsonMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@AllArgsConstructor
@Slf4j
public abstract class AbstractPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String channelName;
    private final JsonMapper<T> jsonMapper;

    public void publish(T object) {
        String jsonFromObject = jsonMapper.convertObjectToJson(object);
        redisTemplate.convertAndSend(channelName, jsonFromObject);
    }
}