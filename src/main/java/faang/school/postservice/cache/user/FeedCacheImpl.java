package faang.school.postservice.cache.user;

import faang.school.postservice.cache.post.PostCache;
import faang.school.postservice.dto.feed.FeedRequest;
import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class FeedCacheImpl implements FeedCache {
    private static final String KEY_PATTERN = "feed:%s";
    @Value("${spring.data.redis.cache.feed.ttl}")
    private int ttl;
    @Value("${spring.data.redis.cache.feed.limit}")
    private int limit;

    private final RedisTemplate<String, Long> cache;
    private final PostCache postCache;

    private String getKey(Long followerId) {
        return String.format(KEY_PATTERN, followerId);
    }

    @Override
    public void add(long followerId, long postId, Instant createdAt) {
        String key = getKey(followerId);
        Double score = cache.opsForZSet().score(key, createdAt.toEpochMilli());
        if (score != null) {
            cache.opsForZSet().add(key, postId, score);
        }
        trimFeed(key);
    }

    @Override
    public void addAll(List<Long> followersIds, long postId, Instant createdAt) {
        double score = createdAt.toEpochMilli();

        cache.executePipelined((RedisCallback<Object>) connection -> {
            for (Long followerId : followersIds) {
                String key = getKey(followerId);

                byte[] rawKey = cache.getStringSerializer().serialize(key);
                RedisSerializer<Long> valueSerializer = (RedisSerializer<Long>) cache.getValueSerializer();
                byte[] rawValue = valueSerializer.serialize(postId);

                if (rawKey != null && rawValue != null) {
                    connection.zAdd(rawKey, score, rawValue);
                }
                trimFeed(key);
            }
            return null;
        });
    }

    @Override
    public List<PostDto> getUserFeed(long userId, FeedRequest request, int perPage) {
        String key = getKey(userId);
        Set<Long> ids;
        if (request.searchAfter() == null) {
            ids = cache.opsForZSet()
                    .reverseRange(key, 0, perPage - 1);
        } else {
            Double afterScore = cache.opsForZSet().score(key, request.searchAfter());
            if (afterScore == null) {
                return new ArrayList<>();
            }
            ids = cache.opsForZSet()
                    .reverseRangeByScore(key, 0, afterScore, 0, perPage);
        }
        if (ids == null) {
            return new ArrayList<>();
        }
        return postCache.getAll(new ArrayList<>(ids));
    }

    private void trimFeed(String key) {
        cache.opsForZSet().removeRange(key, 0, -limit - 1);
    }
}
