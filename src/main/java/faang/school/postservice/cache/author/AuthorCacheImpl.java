
package faang.school.postservice.cache.author;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.post.UserFeignService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class AuthorCacheImpl implements AuthorCache {
    private static final String KEY_AUTHOR_PATTERN = "authors:%s";
    private static final String LOCK_KEY_PATTERN = "lock:user:%d";
    @Value("${spring.data.redis.cache.authors.ttl}")
    private int ttl;
    @Value("${spring.data.redis.cache.authors.lock-sec-ttl}")
    private int lockTtl;
    @Value("${spring.data.redis.cache.authors.waiting-sec-lock}")
    private int lockWaiting;

    private final RedisTemplate<String, UserDto> cache;
    private final UserFeignService userFeignService;
    private final RedissonClient redissonClient;

    private String getKey(Long authorId) {
        return String.format(KEY_AUTHOR_PATTERN, authorId);
    }

    private String getLock(Long authorId) {
        return String.format(LOCK_KEY_PATTERN, authorId);
    }

    @Override
    public void set(UserDto dto) {
        if (dto != null) {
            String key = getKey(dto.id());
            cache.opsForValue().set(key, dto, ttl, TimeUnit.SECONDS);
        }
    }

    @Override
    public UserDto get(long id) {
        String key = getKey(id);
        UserDto userDto = cache.opsForValue().get(key);
        if (userDto == null) {
            RLock lock = redissonClient.getLock(getLock(id));
            boolean locked = false;
            try {
                locked = lock.tryLock(lockWaiting, lockTtl, TimeUnit.SECONDS);
                if (locked) {
                    userDto = userFeignService.getUserOrFail(id);
                    set(userDto);
                } else {
                    userDto = cache.opsForValue().get(key);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } finally {
                if (locked && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        return userDto;
    }
}
