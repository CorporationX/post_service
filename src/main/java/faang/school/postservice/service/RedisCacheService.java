package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.RedisCacheException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${redis.cache.ttl.post}")
    private long ttlPost;

    @Value("${redis.cache.time-unit.post}")
    String timeUnitPost;

    @Value("${redis.cache.ttl.author}")
    private long ttlAuthor;

    @Value("${redis.cache.time-unit.author}")
    String timeUnitAuthor;

    public void savePost(PostDto postDto) {
        String key = "post:" + postDto.getId();
        saveToCache(postDto, timeUnitPost, key, ttlPost);
    }

    public PostDto getPost(long postId) {
        String key = "post:" + postId;
        return getFromCache(key, PostDto.class);
    }

    public void saveAuthor(UserDto userDto) {
        String key = "author:" + userDto.getId();
        saveToCache(userDto, timeUnitAuthor, key, ttlAuthor);
    }

    public UserDto getAuthor(long authorId) {
        String key = "author:" + authorId;
        return getFromCache(key, UserDto.class);
    }

    private void saveToCache(Object dto, String timeUnit, String key, long ttl) {
        try {
            String postJson = objectMapper.writeValueAsString(dto);
            TimeUnit timeUnitString = TimeUnit.valueOf(timeUnit);
            redisTemplate.opsForValue().set(key, postJson, ttl, timeUnitString);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private <T> T getFromCache(String key, Class<T> classType) {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json == null) {
            log.error("Object of " + classType + " with key " + key + " is missing from the cache");
            throw new RedisCacheException("Object of " + classType + " with key " + key + " is missing from the cache");
        }
        try {
            return objectMapper.readValue(json, classType);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
