package faang.school.postservice.repository.redisCache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
public class RedisFeedRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String FEED_PREFIX;
    private final int FEED_LIMIT;
    private final ZSetOperations<String, Object> zSetOperations;

    public RedisFeedRepository(RedisTemplate<String, Object> redisTemplate,
                               @Value("${spring.data.redis.cache.feed.prefix:feed}") String feedPrefix,
                               @Value("${spring.data.redis.cache.feed.limit:20}") int feedLimit) {
        this.redisTemplate = redisTemplate;
        this.zSetOperations = redisTemplate.opsForZSet();
        FEED_PREFIX = feedPrefix;
        FEED_LIMIT = feedLimit;
    }

    public void addPostToFeed(Long subscriberId, Long postId) {
        long timestamp = System.currentTimeMillis();
        String feedKey = getFeedKey(subscriberId);
        zSetOperations.add(feedKey, postId, timestamp);
    }

    public Optional<List<Long>> getAllFeed(Long subscriberId) {
        String feedKey = getFeedKey(subscriberId);
        Set<Object> rawFeed = zSetOperations.reverseRange(feedKey, 0, -1);

        if (rawFeed == null || rawFeed.isEmpty()) {
            return Optional.empty(); // Возвращаем пустой Optional
        }

        List<Long> feedSet = rawFeed.stream()
                .filter(Objects::nonNull)
                .map(item -> (Long) item)
                .toList();

        return Optional.of(feedSet);
    }

    public List<Long> getFeed(Long subscriberId, int startPost) {
        String feedKey = getFeedKey(subscriberId);
        Set<Object> raws = zSetOperations.reverseRange(feedKey, startPost, startPost + FEED_LIMIT);
        return raws.stream()
                .filter(Objects::nonNull)
                .map(item -> (Long) item)
                .toList();
    }

    public void trimFeed(Long subscriberId, int limit) {
        String feedKey = getFeedKey(subscriberId);
        zSetOperations.removeRange(feedKey, 0, -(limit + 1));
    }

    public void clearFeed(Long subscriberId) {
        String feedKey = getFeedKey(subscriberId);
        redisTemplate.delete(feedKey);
    }

    public void saveFeed(Long subscriberId, Map<Long, Long> postIdsTimestamp) {
        if (postIdsTimestamp.isEmpty()) {
            return;
        }

        String feedKey = FEED_PREFIX + subscriberId;
        postIdsTimestamp.forEach((postId, timestamp) -> {
            zSetOperations.add(feedKey, postId, timestamp);
        });
    }

    private String getFeedKey(Long subscriberId) {
        return FEED_PREFIX + subscriberId;
    }

    public void deletePostToFeed(Long subscriberId, Long postId) {
        String feedKey = getFeedKey(subscriberId);
        zSetOperations.remove(feedKey, postId);
    }
}
