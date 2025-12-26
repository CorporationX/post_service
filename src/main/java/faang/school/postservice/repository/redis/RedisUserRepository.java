package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.user.CacheUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisUserRepository {

    private static final String KEY_PREFIX = "users:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.user-repository.ttl}")
    private Duration ttl;

    public void save(CacheUserDto cacheUserDto) {
        if (cacheUserDto == null || cacheUserDto.getId() == null) {
            log.warn("Cannot cache null user or user ID");
            return;
        }

        String key = KEY_PREFIX + cacheUserDto.getId();
        try {
            redisTemplate.opsForValue().set(key, cacheUserDto, ttl);
            log.debug("Cached user in Redis: {}", key);
        } catch (Exception e) {
            log.error("Failed to cache user in Redis: {}", key, e);
        }
    }

    public Optional<CacheUserDto> findById(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        String key = KEY_PREFIX + userId;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value instanceof CacheUserDto cacheUserDto) {
                return Optional.of(cacheUserDto);
            } else if (value != null) {
                log.warn("Unexpected type in Redis for key {}: {}", key, value.getClass());
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Failed to fetch user from Redis: {}", key, e);
            return Optional.empty();
        }
    }
}