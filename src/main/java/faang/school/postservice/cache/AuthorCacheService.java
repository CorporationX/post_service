package faang.school.postservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class AuthorCacheService {

    private final RedisTemplate<String, Long> redisTemplate;

    @Value("${cache.authors-key}")
    private String authorsKey;

    @Value("${cache.expiration-hours}")
    private int expirationHours;

    public void cacheAuthor(Long authorId) {
        if (authorId == null) return;

        SetOperations<String, Long> ops = redisTemplate.opsForSet();
        ops.add(authorsKey, authorId);
        redisTemplate.expire(authorsKey, Duration.ofHours(expirationHours));
    }

    public boolean isAuthorCached(Long authorId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet()
                .isMember(authorsKey, authorId));
    }
}

