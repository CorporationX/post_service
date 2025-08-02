package faang.school.postservice.repository.feed;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.repository.FeedCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
@RequiredArgsConstructor
public class FeedCacheRepositoryImpl implements FeedCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public Optional<ConcurrentLinkedDeque<Long>> get(long userId) {
        HashOperations<String, String, ConcurrentLinkedDeque<Long>> hashOps = redisTemplate.opsForHash();

        return Optional.ofNullable(hashOps.get(redisProperties.getCacheNames().feed(), String.valueOf(userId)));
    }

    @Override
    public void set(long userId, ConcurrentLinkedDeque<Long> postIds) {
        redisTemplate.opsForHash().put(redisProperties.getCacheNames().feed(), String.valueOf(userId), postIds);
        redisTemplate.expire(redisProperties.getCacheNames().feed(), redisProperties.getCacheDuration().feed());
    }
}
