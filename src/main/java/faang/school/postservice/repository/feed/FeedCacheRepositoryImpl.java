package faang.school.postservice.repository.feed;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.repository.FeedCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FeedCacheRepositoryImpl implements FeedCacheRepository {
    private final RedisTemplate<Long, Long> feedRedisTemplate;
    private final RedisProperties redisProperties;

    private final int DEFAULT_OFFSET = 0;

    @Override
    public Optional<Set<Long>> get(long userId) {
        return get(userId, DEFAULT_OFFSET);
    }

    @Override
    public Optional<Set<Long>> get(long userId, int offset) {
        if (feedRedisTemplate.opsForZSet().size(userId) == null) {
            return Optional.of(Set.of());
        }
        Set<Long> range = feedRedisTemplate.opsForZSet().range(userId, offset, redisProperties.getMaxFeedSize());
        return Optional.ofNullable(range);
    }

    @Override
    public synchronized void set(long userId, long postId) {
        feedRedisTemplate.opsForZSet().add(userId, postId, System.currentTimeMillis());
        feedRedisTemplate.expire(userId, redisProperties.getCacheDuration().feed());

        Long size = feedRedisTemplate.opsForZSet().size(userId);
        if (size != null && size > redisProperties.getMaxFeedSize()) {
            feedRedisTemplate.opsForZSet().removeRange(userId, redisProperties.getMaxFeedSize(), size - 1);
        }
    }
}
