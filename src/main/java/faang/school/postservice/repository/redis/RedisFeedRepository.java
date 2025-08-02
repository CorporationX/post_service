package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.post.PostPublishedEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisFeedRepository {
    private static final String FEED_PREFIX = "feed_";
    private final RedisTemplate<String, Object> redisTemplate;

    private ZSetOperations<String, Object> zSetOps;

    @PostConstruct
    public void init() {
        zSetOps = redisTemplate.opsForZSet();
    }

    public void updateFeed(PostPublishedEvent event) {
        List<Long> followersIds = event.followersIds();
        long currentTimeMillis = System.currentTimeMillis();
        for (Long followerId : followersIds) {
            zSetOps.add(FEED_PREFIX + followerId, event.postId(), currentTimeMillis);
            zSetOps.removeRange(FEED_PREFIX + followerId, 0, -101);
        }
        log.info("Feed updated");
    }

    public Set<Object> getFeed(Long userId) {
        return zSetOps.reverseRange(FEED_PREFIX + userId, 0, 19);
    }
}
