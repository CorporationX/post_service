package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisAuthorService {

    private final RedisTemplate<String, Long> redisTemplate;

    @Value("${redis.ttl.author}")
    private Duration authorTtl;

    private static final String AUTHOR_KEY_PREFIX = "author:";

    public void cacheAuthor(Long authorId) {
        String key = AUTHOR_KEY_PREFIX + authorId;
        redisTemplate.opsForValue().set(key, authorId, authorTtl);
    }
}
