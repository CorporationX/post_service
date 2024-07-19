package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${redis.cache.post.ttl-post}")
    private long ttlPost;

    @Value("${redis.cache.post.time-unit-post}")
    String timeUnitPost;

    public void savePost(PostDto postDto) {
        String key = "post:" + postDto.getId();
        try {
            String postJson = objectMapper.writeValueAsString(postDto);
            TimeUnit timeUnitString = TimeUnit.valueOf(timeUnitPost);
            redisTemplate.opsForValue().set(key, postJson, ttlPost, timeUnitString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public PostDto getPost(Long postId) {
        String key = "post:" + postId;
        String postJson = (String) redisTemplate.opsForValue().get(key);
        try {
            return objectMapper.readValue(postJson, PostDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
