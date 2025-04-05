package faang.school.postservice.repository.cache;

import faang.school.postservice.config.redis.RedisKey;
import faang.school.postservice.properties.CacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthorRedisRepository implements AuthorCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheProperties cacheProperties;

    public void cacheAuthor(Long author) {
        redisTemplate.opsForSet().add(RedisKey.AUTHOR.name(), author);
        redisTemplate.expire(RedisKey.AUTHOR.name(), cacheProperties.getAuthorTtl());
    }
}
