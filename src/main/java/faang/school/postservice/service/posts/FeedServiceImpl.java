package faang.school.postservice.service.posts;

import faang.school.postservice.dto.post.PostToFeedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.feed.max-size:500}")
    private int maxFeedSize;

    private static final String FEED_KEY_PREFIX = "feed:";

    public void addPostToFeeds(PostToFeedEvent event) {
        long score = event.getCreatedAt();

        for (Long subscriberId : event.getSubscriberIds()) {
            addPostToFeed(subscriberId, event.getPostId(), score);
        }
    }

    private void addPostToFeed(Long subscriberId, long postId, long score) {
        String key = FEED_KEY_PREFIX + subscriberId;

        redisTemplate.opsForZSet().add(key, postId, score);
        redisTemplate.opsForZSet().removeRange(key, 0, -maxFeedSize - 1);
    }
}
