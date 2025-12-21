package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        String processedKey = "processed:post:" + event.postId();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(processedKey))) {
            log.info("Post {} already processed, skipping", event.postId());
            return;
        }

        try {
            savePostDetails(event);
            double score = -event.publishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

            for (Long followerId : followerIds) {
                updateSingleFeed(followerId, event.postId(), score);
            }

            redisTemplate.opsForValue().set(processedKey, "1", Duration.ofDays(feedTtlDays));
            log.info("Successfully updated feeds for {} followers, postId={}",
                    followerIds.size(), event.postId());

        } catch (Exception e) {
            log.error("Failed to process post {}", event.postId(), e);
            throw e;
        }
    }

    private void savePostDetails(PostEventDto event) {
        final String postKey = "post:" + event.postId();

        Map<String, String> postData = new HashMap<>();
        postData.put("id", event.postId().toString());
        postData.put("content", event.content());
        postData.put("publishedAt", event.publishedAt().toString());

        if (event.authorId() != null) {
            postData.put("authorId", event.authorId().toString());
        }
        if (event.projectId() != null) {
            postData.put("projectId", event.projectId().toString());
        }

        redisTemplate.opsForHash().putAll(postKey, postData);
        redisTemplate.expire(postKey, Duration.ofDays(feedTtlDays));
    }

    private void updateSingleFeed(Long followerId, Long postId, double score) {
        String feedKey = "feed:" + followerId;

        redisTemplate.opsForZSet().add(feedKey, postId.toString(), score);
        redisTemplate.opsForZSet().removeRange(feedKey, maxFeedSize, -1);
        redisTemplate.expire(feedKey, Duration.ofDays(feedTtlDays));
    }
}
