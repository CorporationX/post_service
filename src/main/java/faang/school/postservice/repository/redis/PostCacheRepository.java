package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.redis.cash.PostCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(PostCache postCache, long timeout) {
        redisTemplate.opsForValue().set(postCache.getId(), postCache, timeout, TimeUnit.SECONDS);
        log.info("Post saved in cache: {}", postCache);
    }

    public PostCache get(String id, long timeout) {
        PostCache postCache = (PostCache) redisTemplate.opsForValue().getAndExpire(id, timeout, TimeUnit.SECONDS);
        log.info("Post retrieved from cache: {}", id);
        return postCache;
    }

    public void delete(String id) {
        redisTemplate.delete(id);
        log.info("Post deleted from cache: {}", id);
    }

    public void update(PostCache postCache, Long timeout) {
        redisTemplate.opsForValue().set(postCache.getId(), postCache, timeout, TimeUnit.SECONDS);
        log.info("Post updated in cache: {}", postCache);
    }
}
