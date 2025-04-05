package faang.school.postservice.repository.cache;

import faang.school.postservice.config.redis.RedisKey;
import faang.school.postservice.model.event.LikeEvent;
import faang.school.postservice.properties.CacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeRedisRepository implements LikeCacheRepository {
    private final RedisTemplate<String, LikeEvent> redisTemplate;
    private final CacheProperties cacheProperties;

    @Override
    public void cacheLike(Long postId, LikeEvent likeEvent) {
        redisTemplate.opsForHash().put(RedisKey.LIKE.name(), postId, likeEvent);
        redisTemplate.expire(RedisKey.LIKE.name(), cacheProperties.getPostTtl());
    }
}
