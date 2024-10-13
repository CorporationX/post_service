package faang.school.postservice.repository.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

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

    public void addPost(Long subscriberId, Long postId) {
        String key = FEED_KEY_PREFIX + subscriberId;
        cacheRedisTemplate.opsForZSet().add(key, postId, System.currentTimeMillis());
        cacheRedisTemplate.opsForZSet().removeRange(key, 0, (long) -feedMaxSize - 1);
    }

    public void addPost(List<Long> subscribersIds, Long postId) {
        subscribersIds.forEach(subscriberId -> addPost(subscriberId, postId));
    }

    public List<Long> getUserFeed(Long subscriberId, int offset) {
        String key = FEED_KEY_PREFIX + subscriberId;
        long end = offset + pageSize - 1;

        Set<Object>
    }
}
