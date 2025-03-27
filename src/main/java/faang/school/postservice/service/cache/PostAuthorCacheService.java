package faang.school.postservice.service.cache;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class PostAuthorCacheService {

    public final RedisTemplate<String, Long> redisTemplate;
    private final String authorsKey;
    private final Duration ttl;

    public PostAuthorCacheService(RedisTemplate<String, Long> redisTemplate,
                                  @Value("${cache.authors.collection:comment_authors}") String authorsKey,
                                  @Value("${cache.authors.ttl:86400}") long ttlSeconds) {
        this.redisTemplate = redisTemplate;
        this.authorsKey = authorsKey;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    @Async("postAuthorCacheExecutor")
    public void cachePostAuthor(Long authorId) {
        if (authorId == null) {
            log.warn("AuthorId is null. Skipping caching operation.");
            return;
        }

        log.info("Starting caching operation for authorId {} in key '{}' with TTL {} seconds",
                authorId, authorsKey, ttl.getSeconds());

        Object result = redisTemplate.execute(new SessionCallback<Object>() {
            @SuppressWarnings("unchecked")
            @Override
            public Object execute(@NotNull RedisOperations operations) throws DataAccessException {
                RedisOperations<String, Long> ops = (RedisOperations<String, Long>) operations;
                ops.multi();
                ops.opsForSet().add(authorsKey, authorId);
                ops.expire(authorsKey, ttl);
                return ops.exec();
            }
        });

        log.info("Completed caching operation for authorId {}. Transaction result: {}", authorId, result);
    }
}
