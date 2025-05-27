package faang.school.postservice.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthorCacheService {
    private static final String AUTHOR_CACHE_KEY = "newsfeed:authors";

    private final RedisTemplate<String, String> redisTemplate;

    public void cacheAuthor(String userId, Duration ttl) {
        redisTemplate.opsForSet().add(AUTHOR_CACHE_KEY, userId);
        redisTemplate.expire(AUTHOR_CACHE_KEY, ttl);
    }

}
