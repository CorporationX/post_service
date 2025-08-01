package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.RedisAuthorCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisAuthorCacheImpl implements RedisAuthorCache {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.cache.author_cache_ttl_days}")
    private int ttl;

    public void save(String key, Object object) {
        log.debug("Start saving author with body {} in cache", object);
        redisTemplate.opsForValue().set(key, object, ttl, TimeUnit.DAYS);
        log.debug("Author with body {} was saved in cache", object);
    }

    public UserDto getAuthor(String key) {
        try {
            UserDto userDto = (UserDto) redisTemplate.opsForValue().get(key);
            if (userDto == null) {
                log.debug("User for key: {} was not found in cache", key);
            }

            return userDto;
        } catch (Exception e) {
            log.error("Something gone wrong while getting author for key: {}", key, e);
            throw new CacheException(String.format("Get author from Cache Error key: %s", key), e);
        }
    }
}
