package faang.school.postservice.repository.rediscache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FeedRedisRepository {

    private final RedisTemplate<String, List<String>> redisTemplate;

    @Value("${spring.max_feed_size}")
    private int maxFeedSize;

    @Value("${spring.ttl_seconds}")
    private int ttlSeconds;

    public synchronized void save(String key, String postId) {
        log.info("Saving post with ID: {} to Redis", postId);

        List<String> postIds = redisTemplate.opsForValue().get(key);

        if (postIds == null) {
            postIds = new ArrayList<>();
        }

        postIds.add(0, postId);

        if (postIds.size() > maxFeedSize) {
            postIds.remove(postIds.size() - 1);
        }

        redisTemplate.opsForValue().set(key, postIds, Duration.ofSeconds(ttlSeconds));
    }

    public synchronized List<String> getPostIdsFromCache(String userId, String postId) {
        log.info("Fetching posts from Redis for user with ID: {}", userId);

        List<String> postIds = redisTemplate.opsForValue().get(userId);

        if (postIds == null || postIds.isEmpty()) {
            return new ArrayList<>();
        }

        int startIndex = 0;

        if (postId != null) {
            int index = postIds.indexOf(postId);
            if (index >= 0) {
                startIndex = index;
            }
        }

        int endIndex = Math.min(startIndex + 20, postIds.size());

        return postIds.subList(startIndex, endIndex);
    }
}
