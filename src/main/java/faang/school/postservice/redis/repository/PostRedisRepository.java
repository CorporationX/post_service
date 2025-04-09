package faang.school.postservice.redis.repository;

import faang.school.postservice.redis.model.entity.PostCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostRedisRepository {

    @Qualifier("redisCacheTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    public void savePost(PostCache post, long ttl, String keyPrefix) {
        String key = keyPrefix + post.getId();
        redisTemplate.opsForValue().set(key, post);
        redisTemplate.expire(key, Duration.ofSeconds(ttl));
        log.info("Post with ID {} saved to Redis with TTL: {}s", post.getId(), ttl);
    }

    public void saveAll(List<PostCache> posts, long ttl, String keyPrefix) {
        posts.forEach(post -> savePost(post, ttl, keyPrefix));
    }

    public void incrementHashValue(String key, String field) {
        redisTemplate.opsForHash().increment(key, field, 1);
    }

    public List<Object> getPostsByKeys(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    public RLock acquireLock(String key) {
        return redissonClient.getLock(key);
    }
}
