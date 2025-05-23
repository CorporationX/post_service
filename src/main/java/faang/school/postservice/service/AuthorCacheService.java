package faang.school.postservice.service;

import faang.school.postservice.entity.CachedAuthor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorCacheService {

    private final RedisTemplate<String, CachedAuthor> redisTemplate;

    @Value("${spring.redis.ttl.author}")
    private long timeToLive;

    private static final String AUTHOR_CACHED_KEY = "authors:";

    public void cacheAuthor(Long userId, String username) {
        CachedAuthor cachedAuthor = CachedAuthor.builder()
                .authorId(userId)
                .username(username)
                .ttl(timeToLive)
                .build();

        redisTemplate.opsForValue().set(
                AUTHOR_CACHED_KEY + userId,
                cachedAuthor
        );

    }
}
