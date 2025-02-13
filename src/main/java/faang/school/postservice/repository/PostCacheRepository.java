package faang.school.postservice.repository;

import faang.school.postservice.dto.post.RedisPostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class PostCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${cache.post-ttl-duration}")
    private Duration ttlDuration;

    public void savePostToCache(RedisPostDto post) {
        String key = "post:" + post.id();
        redisTemplate.opsForValue().set(key, post, ttlDuration);
    }

    public RedisPostDto getPostFromCache(String postId) {
        String key = "post:" + postId;
        return (RedisPostDto) redisTemplate.opsForValue().get(key);
    }
}
