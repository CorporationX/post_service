package faang.school.postservice.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@RequiredArgsConstructor
public class Feed {
    private final RedisTemplate<String, ConcurrentLinkedDeque<Long>> dequeRedisTemplate;

    @Value("${cache.expiration-hours}")
    private int expirationHours;
    @Value("${cache.max-posts}")
    private int maxPosts;
    @Value("${feed.feed-key}")
    String feedKey;

    public void save(Long userId, Long postId) {
        ConcurrentLinkedDeque<Long> currentDeque = get(userId);
        if (currentDeque == null) {
            currentDeque = new ConcurrentLinkedDeque<>();
        } else if (currentDeque.size() == maxPosts) {
            currentDeque.poll();
        }
        currentDeque.add(postId);

        dequeRedisTemplate.opsForValue().set(feedKey + userId, currentDeque, Duration.ofHours(expirationHours));
    }

    public ConcurrentLinkedDeque<Long> get(Long userId) {
        return dequeRedisTemplate.opsForValue().get(feedKey + userId);
    }
}
