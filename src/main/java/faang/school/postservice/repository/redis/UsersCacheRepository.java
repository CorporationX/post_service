package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.redis.cash.UserCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UsersCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(UserCache userCache, long timeout) {
        redisTemplate.opsForValue().set(userCache.getId(), userCache, timeout, TimeUnit.SECONDS);
        log.info("User saved in cache: {}", userCache);
    }

    public UserCache get(String id, long timeout) {
        UserCache userCache = (UserCache) redisTemplate.opsForValue().getAndExpire(id, timeout, TimeUnit.SECONDS);
        log.info("User retrieved from cache: {}", id);
        return userCache;
    }

    public void delete(String id) {
        redisTemplate.delete(id);
        log.info("User deleted from cache: {}", id);
    }

    public void update(UserCache userCache, Long timeout) {
        redisTemplate.opsForValue().set(userCache.getId(), userCache, timeout, TimeUnit.SECONDS);
        log.info("User updated in cache: {}", userCache);
    }
}
