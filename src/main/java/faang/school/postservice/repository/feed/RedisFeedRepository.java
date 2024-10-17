package faang.school.postservice.repository.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisFeedRepository {
    private final RedisTemplate<String, Object> cacheRedisTemplate;
    private static final String FEED_KEY_PREFIX = "feed:";
    @Value("${spring.data.redis.cache.feed.maxSize}")
    private int feedMaxSize;
    @Value("${spring.data.redis.cache.feed.pageSize}")
    private int pageSize;

    public void addPost(Long subscriberId, Long postId, LocalDateTime publishedAt) {
        String key = FEED_KEY_PREFIX + subscriberId;
        double score = publishedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        cacheRedisTemplate.opsForZSet().add(key, postId, score);
        cacheRedisTemplate.opsForZSet().removeRange(key, 0, (long) -feedMaxSize - 1);
    }

    public void addPost(List<Long> subscribersIds, Long postId, LocalDateTime publishedAt) {
        subscribersIds.forEach(subscriberId -> addPost(subscriberId, postId, publishedAt));
    }

    public List<Long> getUserFeed(Long subscriberId, int offset) {
        String key = FEED_KEY_PREFIX + subscriberId;
        long end = offset + pageSize - 1;

        Set<Object>
    }

    public void deletePostFromAllFeeds(Long postId) {
        Set<String> feedKeys = cacheRedisTemplate.keys(FEED_KEY_PREFIX + "*");
        if (feedKeys != null) {
            for (String key : feedKeys) {
                cacheRedisTemplate.opsForZSet().remove(key, postId);
            }
        }
    }
}
