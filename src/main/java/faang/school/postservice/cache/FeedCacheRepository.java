package faang.school.postservice.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedCacheRepository {

    @Value("${cache.feed.collection}")
    private String collection;

    @Value("${cache.feed.ttl-days}")
    private int ttlDays;

    @Value("${cache.feed.max-size}")
    private int maxSize;

    private final StringRedisTemplate redisTemplate;

    public void save(Long followerId, Long postId, LocalDateTime postCreatedAt) {
        double score = postCreatedAt.toEpochSecond(ZoneOffset.UTC);
        String key = collection + followerId;

        redisTemplate.opsForZSet().add(key, postId.toString(), score);
        log.debug("Saved new post to cache. Key {}, post id {}", key, postId);

        Long size = redisTemplate.opsForZSet().size(key);
        if (size != null && size > maxSize) {
            redisTemplate.opsForZSet().removeRange(key, 0, size - maxSize - 1);

        }

        redisTemplate.expire(key, ttlDays, TimeUnit.DAYS);
    }

    public List<Long> get(Long followerId, Long lastPostId, int size) {
        String key = collection + followerId;

        if (lastPostId == null) {
            Set<String> ids = redisTemplate.opsForZSet().reverseRange(key, 0, size - 1);
            return convertToLong(ids);
        }

        Double lastScore = redisTemplate.opsForZSet().score(key, lastPostId.toString());

        if (lastScore == null) {
            return get(followerId, null, size);
        }

        Set<String> ids = redisTemplate.opsForZSet()
                .reverseRangeByScore(key, 0, lastScore - 0.001, 0, size);
        return convertToLong(ids);
    }

    private List<Long> convertToLong(Set<String> ids) {
        return ids.stream().map(Long::parseLong).collect(Collectors.toList());
    }
}