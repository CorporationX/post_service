package faang.school.postservice.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, ConcurrentLinkedDeque<Long>> redisTemplateDeque;
    private final ObjectMapper mapper;

    public void saveToRedisWithTtl(String key, Object object, Long ttl, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, object, ttl, timeUnit);
    }

    public void saveToRedis(String key, ConcurrentLinkedDeque<Long> deque) {
        redisTemplateDeque.opsForValue().set(key, deque);
    }


    public ConcurrentLinkedDeque<Long> getAndDeleteFeed(String key) {
        Object object = redisTemplateDeque.opsForValue().getAndDelete(key);
        if (object == null) {
            return new ConcurrentLinkedDeque<>();
        }
        ConcurrentLinkedDeque<Long> deque = new ConcurrentLinkedDeque<>((List<Long>) object);
        redisTemplateDeque.delete(key);
        return deque;
    }

    public PostResponseDto getPost(String key) {
        Object object = redisTemplate.opsForValue().get(key);
        return mapper.convertValue(object, PostResponseDto.class);
    }
}
