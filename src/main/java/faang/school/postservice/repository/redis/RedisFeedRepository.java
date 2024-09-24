package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.FeedRedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisFeedRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.cache.ttl.feed}")
    private long timeToLive;

    public void save(FeedRedis feedRedis) {
        String key = "feed:" + feedRedis.getId();
        try {
            redisTemplate.opsForValue().set(key, feedRedis, timeToLive, TimeUnit.SECONDS);
        } catch (RedisConnectionFailureException | JedisConnectionException e) {
            log.error("Failed to save feed for follower with ID: {} in Redis", feedRedis.getId(), e);
        }
    }

    public Optional<FeedRedis> getAllPostIdsByFollowerId(long followerId) {
        String key = "feed:" + followerId;
        try {
            FeedRedis feedRedis = (FeedRedis) redisTemplate.opsForValue().get(key);
            return Optional.ofNullable(feedRedis);
        } catch (RedisConnectionFailureException | JedisConnectionException e) {
            log.error("Failed to fetch feed for follower with ID: {} from Redis", followerId, e);
            return Optional.empty();
        }
    }
}