package faang.school.postservice.cache.author;

import faang.school.postservice.config.properties.cache.author.AuthorCacheProperties;
import faang.school.postservice.dto.cache.AuthorCacheDto;
import faang.school.postservice.mapper.user.UserMapper;
import faang.school.postservice.service.post.UserFeignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import faang.school.postservice.dto.user.UserDto;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthorCacheImpl implements AuthorCache {

    private static final String LOCK_KEY_PATTERN = "lock:author:%d";

    private final RedisTemplate<String, AuthorCacheDto> redisTemplate;
    private final AuthorCacheProperties properties;
    private final UserFeignService userFeignService;
    private final UserMapper userMapper;
    private final RedissonClient redissonClient;

    private String buildCacheKey(long authorId) {
        return properties.keyPrefix() + authorId;
    }

    private String buildLockKey(long authorId) {
        return String.format(LOCK_KEY_PATTERN, authorId);
    }

    @Override
    public void put(AuthorCacheDto author) {
        try {
            String key = buildCacheKey(author.id());
            redisTemplate.opsForValue().set(key, author, properties.ttl());
        } catch (Exception e) {
            log.warn("Redis put(author) failed id={}", author.id(), e);
        }
    }

    @Override
    public AuthorCacheDto get(Long authorId) {
        String key = buildCacheKey(authorId);
        try {
            AuthorCacheDto authorFromCache = redisTemplate.opsForValue().get(key);
            if (authorFromCache != null) {
                return authorFromCache;
            }

            RLock lock = redissonClient.getLock(buildLockKey(authorId));
            boolean locked = false;
            try {
                locked = lock.tryLock(
                        properties.lockWaitSeconds(),
                        properties.lockLeaseSeconds(),
                        TimeUnit.SECONDS);

                if (!locked) {
                    return redisTemplate.opsForValue().get(key);
                }

                authorFromCache = redisTemplate.opsForValue().get(key);
                if (authorFromCache != null) {
                    return authorFromCache;
                }

                UserDto user = userFeignService.getUserOrFail(authorId);
                AuthorCacheDto authorToCache = userMapper.toCacheEntry(user);
                redisTemplate.opsForValue().set(key, authorToCache, properties.ttl());
                return authorToCache;

            } finally {
                if (locked && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Author cache get interrupted authorId={}", authorId, e);
            return null;
        } catch (Exception e) {
            log.warn("Author cache get failed authorId={}", authorId, e);
            return null;
        }
    }

    @Override
    public void delete(Long authorId) {
        try {
            redisTemplate.delete(buildCacheKey(authorId));
        } catch (Exception e) {
            log.warn("Author evict failed authorId={}", authorId, e);
        }
    }
}
