package faang.school.postservice.service.redis;

import faang.school.postservice.exception.RedisOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static faang.school.postservice.contants.ErrorMessage.CACHE_AUTHOR_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorCacheService {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.key-prefix.author-cache}")
    private String authorCacheKey;

    public void cacheAuthor(String userId, Duration ttl) {
        String key = authorCacheKey + ":" + userId;
        try {
            Boolean added = redisTemplate.opsForValue().setIfAbsent(key, "cached", ttl);
            if (Boolean.TRUE.equals(added)) {
                log.info("Author {} cached with TTL {}", userId, ttl);
            } else {
                log.info("Author {} already cached", userId);
            }
        } catch (RuntimeException e) {
            log.error("{}: userId={}, reason={}", CACHE_AUTHOR_ERROR, userId, e.getMessage(), e);
            throw new RedisOperationException(CACHE_AUTHOR_ERROR);
        }
    }

}
