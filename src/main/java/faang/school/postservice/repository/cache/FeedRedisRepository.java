package faang.school.postservice.repository.cache;

import faang.school.postservice.properties.CacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FeedRedisRepository implements FeedCacheRepository {
    private final RedisTemplate<Long, Long> redisTemplate;
    private final CacheProperties cacheProperties;

    @Override
    public void cacheFeed(Long userId, Long postId) {
        redisTemplate.opsForZSet().add(userId, postId, System.currentTimeMillis());
        redisTemplate.expire(userId, cacheProperties.getFeedTtl());

        Long size = redisTemplate.opsForZSet().size(userId);
        if (size != null && size > cacheProperties.getCacheFeedSize()) {
            long removeTo = size - cacheProperties.getCacheFeedSize() - 1;
            redisTemplate.opsForZSet().removeRange(userId, 0, removeTo);
        }
    }

    @Override
    public Set<Long> getPostEvents(Long userId, Long batch) {
        Long size = redisTemplate.opsForZSet().size(userId);
        if (size == null) {
            return Set.of();
        }

        Set<Long> postIds = redisTemplate.opsForZSet().range(userId, Math.min(size - batch, 0), size);
        assert postIds != null;
        postIds.forEach(id -> removePost(id, userId));
        return postIds;
    }

    @Override
    public void removePost(Long postEvent, Long userId) {
        redisTemplate.opsForZSet().remove(userId, postEvent);
    }

}
