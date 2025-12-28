package faang.school.postservice.cache;

import faang.school.postservice.cache.model.author.AuthorCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuthorCacheRepository {

    @Value("${cache.users.collection}")
    private String collection;

    private final StringRedisTemplate redisTemplate;

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