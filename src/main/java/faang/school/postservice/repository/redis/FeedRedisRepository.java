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
import java.util.List;

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

    @Value("${redis.schema.feed.key}")
    private String keyFeed;
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
        String key = String.format(keyFeed, userId);
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
}