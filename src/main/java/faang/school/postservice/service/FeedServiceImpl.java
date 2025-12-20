package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.feed.max-size}")
    private int maxFeedSize;

    @Value("${app.feed.ttl-days:1}")
    private int feedTtlDays;

    @Override
    public void updateFeeds(PostEventDto event) {
        List<Long> followerIds = event.followerIds();
        if (followerIds == null || followerIds.isEmpty()) {
            log.info("No followers for post {}", event.postId());
            return;
        }

        double score = -event.publishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        for (Long followerId : followerIds) {
            try {
                String feedKey = "feed:" + followerId;

                redisTemplate.opsForZSet().add(feedKey, event.postId(), score);
                redisTemplate.opsForZSet().removeRange(feedKey, maxFeedSize, -1);
                redisTemplate.expire(feedKey, Duration.ofDays(feedTtlDays));
                log.debug("Updated feed for follower {}: added post {}", followerId, event.postId());

            } catch (Exception e) {
                log.error("Failed to update feed for follower {}, post {}", followerId, event.postId(), e);
                throw e;
            }
        }

        log.info("Successfully updated feeds for {} followers, postId={}",
                followerIds.size(), event.postId());
    }
}
