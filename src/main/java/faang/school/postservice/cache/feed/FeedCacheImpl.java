package faang.school.postservice.cache.feed;

import faang.school.postservice.config.properties.cache.feed.FeedCacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FeedCacheImpl implements FeedCache {

    private final StringRedisTemplate stringRedis;
    private final FeedCacheProperties props;

    private String buildFeedKey(long followerId) {
        return props.keyPrefix() + followerId;
    }

    @Override
    public void add(long followerId, long postId, Instant publishedAt) {
        String feedKey = buildFeedKey(followerId);
        String memberId = Long.toString(postId);
        double publishScore = publishedAt.toEpochMilli();

        ZSetOperations<String, String> zset = stringRedis.opsForZSet();

        zset.add(feedKey, memberId, publishScore);

        zset.removeRange(feedKey, 0, -(props.maxSize() + 1L));

        if (props.ttlSeconds() > 0) {
            stringRedis.expire(feedKey, Duration.ofSeconds(props.ttlSeconds()));
        }
    }

    @Override
    public void addAll(List<Long> followerIds, long postId, Instant publishedAt) {
        if (followerIds == null || followerIds.isEmpty()) {
            return;
        }

        List<Long> normalizedFollowerIds = new ArrayList<>(followerIds.size());
        for (Long id : followerIds) {
            if (id != null) normalizedFollowerIds.add(id);
        }
        if (normalizedFollowerIds.isEmpty()) {
            return;
        }

        String memberId = Long.toString(postId);
        double publishScore = publishedAt.toEpochMilli();
        long maxSize = props.maxSize();
        int ttlSeconds = props.ttlSeconds();
        long removeUntil = -(maxSize + 1L);

        stringRedis.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection conn = new DefaultStringRedisConnection(connection);
            for (Long followerId : normalizedFollowerIds) {
                String feedKey = buildFeedKey(followerId);
                conn.zAdd(feedKey, publishScore, memberId);
                conn.zRemRange(feedKey, 0, removeUntil);
                if (ttlSeconds > 0) {
                    conn.expire(feedKey, ttlSeconds);
                }
            }
            return null;
        });
    }

    @Override
    public List<Long> getIds(long userId, Long afterPostId, int limit) {
        final String feedKey = buildFeedKey(userId);
        final ZSetOperations<String, String> zset = stringRedis.opsForZSet();

        Set<String> range;
        if (afterPostId == null) {
            range = zset.reverseRange(feedKey, 0, Math.max(0, limit - 1));
        } else {
            Long cursorRank = zset.reverseRank(feedKey, String.valueOf(afterPostId));
            if (cursorRank == null) {
                range = zset.reverseRange(feedKey, 0, Math.max(0, limit - 1));
            } else {
                range = zset.reverseRange(feedKey, cursorRank + 1, cursorRank + limit);
            }
        }

        if (range == null || range.isEmpty()) {
            return List.of();
        }

        List<Long> ids = new ArrayList<>(range.size());
        for (String s : range) {
            ids.add(Long.valueOf(s));
        }
        return ids;
    }
}
