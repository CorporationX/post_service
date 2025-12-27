package faang.school.postservice.service;

import java.time.Instant;

import faang.school.postservice.config.FeedRedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    /*  Lua script for Redis, to provide ACID transaction as much as possible
        -- KEYS[1] = feed key
        -- ARGV[1] = score
        -- ARGV[2] = member (postId)
        -- ARGV[3] = maxSize
        return 1 if post added and 0 in case of duplicate
     */
    private static final DefaultRedisScript<Long> ADD_AND_TRIM_SCRIPT;

    static {
        ADD_AND_TRIM_SCRIPT = new DefaultRedisScript<>();
        ADD_AND_TRIM_SCRIPT.setResultType(Long.class);
        ADD_AND_TRIM_SCRIPT.setScriptText(
                "local added = redis.call('ZADD', KEYS[1], 'NX', ARGV[1], ARGV[2]) " +
                        "local size = redis.call('ZCARD', KEYS[1]) " +
                        "local maxSize = tonumber(ARGV[3]) " +
                        "if size > maxSize then " +
                        "  local extra = size - maxSize " +
                        "  redis.call('ZREMRANGEBYRANK', KEYS[1], 0, extra - 1) " +
                        "end " +
                        "return added"
        );
    }

    private final StringRedisTemplate redis;
    private final FeedRedisProperties props;

    @Override
    public void addPostToFeed(long followerId, long postId, Instant occurredAt) {
        String key = buildKey(followerId);

        // in case of close occurredAt time, we add uniqueness with postId
        long score = occurredAt.toEpochMilli() * 1_000 + (postId % 1_000);

        Long added = redis.execute(
                ADD_AND_TRIM_SCRIPT,
                List.of(key),
                String.valueOf(score),
                String.valueOf(postId),
                String.valueOf(props.getMaxSize())
        );

        if (added == 1) {
            log.debug(
                    "Post added to feed: followerId={}, postId={}, score={}, maxSize={}, key={}",
                    followerId,
                    postId,
                    score,
                    props.getMaxSize(),
                    key
            );
        } else {
            log.debug(
                    "Duplicate PostCreatedEvent ignored: followerId={}, postId={}, key={}",
                    followerId,
                    postId,
                    key
            );
        }
    }

    @Override
    public List<Long> getLatestPosts(long followerId, int limit) {
        String key = buildKey(followerId);

        var range = redis.opsForZSet().reverseRange(key, 0, limit - 1);
        if (range == null || range.isEmpty()) {
            return List.of();
        }

        return range.stream().map(Long::parseLong).toList();
    }

    private String buildKey(long followerId) {
        return props.getKeyPrefix() + followerId;
    }
}