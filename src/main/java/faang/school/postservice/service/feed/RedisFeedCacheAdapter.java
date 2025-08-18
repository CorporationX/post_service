package faang.school.postservice.service.feed;

import faang.school.postservice.config.properties.FeedCacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RedisFeedCacheAdapter implements FeedCachePort {

    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedCacheProperties properties;

    @Override
    public void addToFeed(Long userId, Long postId, double score) {
        if (userId == null || postId == null) {
            return;
        }
        String key = feedKey(userId);
        redisTemplate.opsForZSet().add(key, String.valueOf(postId), score);
    }

    @Override
    public void trimToMaxSize(Long userId, int maxSize) {
        if (userId == null || maxSize <= 0) {
            return;
        }
        String key = feedKey(userId);
        Long size = redisTemplate.opsForZSet().zCard(key);
        if (size != null && size > maxSize) {
            long toRemove = size - maxSize;
            redisTemplate.opsForZSet().removeRange(key, 0, toRemove - 1);
        }
    }

    @Override
    public List<Long> getTop(Long userId, int limit) {
        if (userId == null || limit <= 0) {
            return Collections.emptyList();
        }
        String key = feedKey(userId);
        var members = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
        if (members == null) return Collections.emptyList();
        return members.stream()
                .map(m -> (String) m)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getAfter(Long userId, Long afterPostId, int limit) {
        if (userId == null || afterPostId == null || limit <= 0) {
            return Collections.emptyList();
        }
        String key = feedKey(userId);
        Double score = redisTemplate.opsForZSet().score(key, String.valueOf(afterPostId));
        if (score == null) {
            return Collections.emptyList();
        }
        double maxExclusive = score - 0.000001d;
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        var members = zset.reverseRangeByScore(key, Double.NEGATIVE_INFINITY, maxExclusive, 0, limit);
        if (members == null) return Collections.emptyList();
        return members.stream()
                .map(m -> (String) m)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    private String feedKey(Long userId) {
        return properties.getKeyPrefix() + ":" + userId;
    }
}
