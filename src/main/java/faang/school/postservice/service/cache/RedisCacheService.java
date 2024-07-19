package faang.school.postservice.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Object lock = new Object();

    @Value("${spring.data.redis.settings.ttl}")
    private int ttl;

    public void saveToCache(String pattern, Long key, Object value) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);

            synchronized (lock) {
                redisTemplate.opsForHash().put(pattern, key, jsonValue);
                redisTemplate.expire(pattern, ttl, TimeUnit.DAYS);
                log.debug("Saved key: {} with value: {} and patternType: {}", key, jsonValue, pattern);
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for key: {}", key, e);
            throw new RuntimeException(e);
        }
    }
}
