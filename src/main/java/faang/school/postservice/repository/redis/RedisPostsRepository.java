package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.PostRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Repository("redisPostsRepository")
@RequiredArgsConstructor
public class RedisPostsRepository {

    @Qualifier("postRedisTemplate")
    private final RedisTemplate<String, PostRedis> postRedisTemplate;
    private static final String KEY_PREFIX = "Post:";

    public void save(PostRedis postRedis) {
        String key = KEY_PREFIX + postRedis.getId();
        postRedisTemplate.opsForValue().set(key, postRedis);
    }

    public void saveWithTTL(PostRedis postRedis, long ttlInSeconds) {
        String key = KEY_PREFIX + postRedis.getId();
        postRedisTemplate.opsForValue().set(key, postRedis, Duration.ofSeconds(ttlInSeconds));
    }

    public void saveAll(List<PostRedis> posts) {
        Map<String, PostRedis> keyValueMap = posts.stream()
                .collect(Collectors.toMap(
                        post -> KEY_PREFIX + post.getId(),
                        post -> post
                ));
        postRedisTemplate.opsForValue().multiSet(keyValueMap);
    }

    public void saveAllWithTTL(List<PostRedis> posts, long ttlInSeconds) {
        for (PostRedis post : posts) {
            saveWithTTL(post, ttlInSeconds);
        }
    }

    public Optional<PostRedis> findById(Long id) {
        String key = KEY_PREFIX + id;
        PostRedis postRedis = postRedisTemplate.opsForValue().get(key);
        return Optional.ofNullable(postRedis);
    }

    public void deleteById(Long id) {
        String key = KEY_PREFIX + id;
        postRedisTemplate.delete(key);
    }

    public void deleteAllByIds(List<Long> ids) {
        List<String> keys = ids.stream()
                .map(id -> KEY_PREFIX + id)
                .collect(Collectors.toList());
        postRedisTemplate.delete(keys);
    }

    public boolean existsById(Long id) {
        String key = KEY_PREFIX + id;
        Boolean exists = postRedisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    public long getRemainingTtl(Long id) {
        String key = KEY_PREFIX + id;
        Long ttl = postRedisTemplate.getExpire(key);
        return ttl != null ? ttl : -2;
    }
}
