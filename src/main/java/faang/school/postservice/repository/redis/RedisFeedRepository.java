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
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
//            FeedRedis feedRedis = (FeedRedis) redisTemplate.opsForZSet().range(key, 0, redisTemplate.opsForZSet().count(key, Double.MIN_VALUE, Double.MAX_VALUE));
//            FeedRedis feedRedis = (FeedRedis) redisTemplate.opsForZSet().range(key, 0, -1);
            return Optional.ofNullable(feedRedis);
        } catch (RedisConnectionFailureException | JedisConnectionException e) {
            log.error("Failed to fetch feed for follower with ID: {} from Redis", followerId, e);
            return Optional.empty();
        }
    }

    public Optional<TreeSet<Long>> getPostsIdsByFollowerId(long followerId, int limit) {
        return getAllPostIdsByFollowerId(followerId)
                .map(feedRedis -> feedRedis.getPostsIds()
                        .stream()
                        .limit(limit)
                        .collect(Collectors.toCollection(TreeSet::new)));
    }

    public Optional<Long> getOnePostIdByFollowerId(long followerId) {
        return getAllPostIdsByFollowerId(followerId)
                .flatMap(feedRedis -> feedRedis.getPostsIds()
                        .stream()
                        .findFirst());
    }
}