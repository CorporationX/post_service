package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.CachedAuthor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisAuthorCacheRepository {
    private final static String PREFIX = "author_";
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.cache.author_cache_ttl_hours}")
    private long ttlHours;
    private HashOperations<String, String, CachedAuthor> hashOpsAuthors;

    @PostConstruct
    public void init() {
        hashOpsAuthors = redisTemplate.opsForHash();
    }

    public void saveAuthor(CachedAuthor author) {
        try {
            hashOpsAuthors.put(PREFIX + author.getId(), author.getId().toString(), author);
            redisTemplate.expire(PREFIX + author.getId(), ttlHours, TimeUnit.HOURS);
            log.debug("Author with id: {} was saved in Cache", author.getId());
        } catch (Exception e) {
            log.error("Something went wrong with save author with id: {} in Cache", author.getId());
        }
    }

    public CachedAuthor getAuthor(Long authorId) {
        return hashOpsAuthors.get(PREFIX + authorId, authorId);
    }
}