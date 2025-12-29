package faang.school.postservice.cache.repository;

import faang.school.postservice.cache.model.author.AuthorCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuthorCacheRepository {

    @Value("${cache.users.collection}")
    private String collection;

    @Value("${cache.users.ttl-days}")
    private int ttlDays;

    private final StringRedisTemplate redisTemplate;

    public void save(AuthorCache authorCache) {
        String key = collection + authorCache.userId();

        Map<String, String> hashData = new HashMap<>() {{
            put(AuthorCache.Fields.userId, authorCache.userId().toString());
            put(AuthorCache.Fields.username, authorCache.username());
        }};

        redisTemplate.opsForHash().putAll(key, hashData);
        log.debug("Saved new author to cache. Key {}, id {}", key, authorCache.userId());

        redisTemplate.expire(key, ttlDays, TimeUnit.DAYS);
    }

    public void saveAll(List<AuthorCache> authorCaches) {
        for (AuthorCache cache : authorCaches) {
            String key = collection + cache.userId();

            Map<String, String> hashData = new HashMap<>() {{
                put(AuthorCache.Fields.userId, cache.userId().toString());
                put(AuthorCache.Fields.username, cache.username());
            }};

            redisTemplate.opsForHash().putAll(key, hashData);
            redisTemplate.expire(key, ttlDays, TimeUnit.DAYS);
        }
    }

    public Optional<AuthorCache> get(Long authorId) {
        String key = collection + authorId;

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        if (entries.isEmpty()) {
            log.debug("User {} not found in author cache repository", authorId);
            return Optional.empty();
        }

        try {
            AuthorCache authorCache = AuthorCache.builder()
                    .userId(authorId)
                    .username(entries.get(AuthorCache.Fields.username).toString())
                    .build();
            return Optional.of(authorCache);
        } catch (Exception e) {
            log.error("Failed to parse cached author {}: {}", authorId, e.getMessage());
            return Optional.empty();
        }
    }
}