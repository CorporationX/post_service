package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.avro.FeedBatchEventAvro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Репозиторий для коллекции feed в Redis
 *
 * @author Linempy
 * @since 24.09.2025
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedRedisRepository {

    private static final String FEED_TEMPLATE = "feed:user:%s";

    @Value("${redis.schema.feed.ttl-day}")
    private Long ttlDays;
    @Value("${redis.schema.feed.max-size}")
    private Integer maxSize;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final DefaultRedisScript<Void> ADD_AND_TRIM = new DefaultRedisScript<>("""
          redis.call('ZADD', KEYS[1], ARGV[1], ARGV[2])
          redis.call('EXPIRE', KEYS[1], ARGV[3])
          local max_size = tonumber(ARGV[4])
          if max_size > 0 then
              local current_size = redis.call('ZCARD', KEYS[1])
              if current_size > max_size then
                  redis.call('ZREMRANGEBYRANK', KEYS[1], 0, current_size - max_size - 1)
              end
          end
          """, Void.class);

    private static final int DAY_IN_SECONDS = 24 * 60 * 60;

    public void updateFeed(String userId, String postId, Instant publishedAt) {
        String key = getFormattedKey(userId);
        long timeUnit = publishedAt.atZone(ZoneOffset.UTC).toEpochSecond();
        long ttlSeconds = ttlDays * DAY_IN_SECONDS;

        redisTemplate.execute(ADD_AND_TRIM, List.of(key),
                timeUnit,
                postId,
                ttlSeconds,
                maxSize);
    }

    public void updateFeedViaBatch(FeedBatchEventAvro event) {
        event.getSubscriberIds()
                .forEach(id -> {
                    updateFeed(id, event.getPostId(), event.getPublishedAt());
                });
        log.info("Сохранение постов в feed в Redis прошло успешно!");
    }

    public List<Long> getFeed(Long lastPostId, Long userId, int size) {
        if (areInvalidParams(userId, size)) {
            return Collections.emptyList();
        }

        String key = getFormattedKey(String.valueOf(userId));

        if (lastPostId == null || lastPostId == 0) {
            long start = 0;
            long end = size - 1;

            return processReturnGetFeed(key, start, end);
        }

        Long rank = redisTemplate.opsForZSet().reverseRank(key, lastPostId);
        if (rank == null) {
            return getFeed(null, userId, size);
        }

        long start = rank + 1;
        long end = rank + size - 1;
        return processReturnGetFeed(key, start, end);
    }

    private List<Long> processReturnGetFeed(String key, long start, long end) {
        Set<Object> rawPosts = redisTemplate.opsForZSet().reverseRange(key, start, end);

        if (rawPosts == null || rawPosts.isEmpty()) {
            return Collections.emptyList();
        }

        return convertToLongList(rawPosts);
    }

    private List<Long> convertToLongList(Set<Object> rawPosts) {
        return rawPosts.stream()
                .map(obj -> Long.valueOf(obj.toString()))
                .toList();
    }

    private boolean areInvalidParams(Long userId, int size) {
        return userId == null || size <= 0;
    }

    private String getFormattedKey(String id) {
        return String.format(FEED_TEMPLATE, id);
    }
}