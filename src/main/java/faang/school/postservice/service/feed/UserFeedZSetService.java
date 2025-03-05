package faang.school.postservice.service.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFeedZSetService {
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.data.cache.feed.feed-size}")
    private int feed_size;

    public void addPostToFeed(Long userId, Long postId, LocalDateTime timestamp) {
        String feedKey = getFeedKey(userId);
        double score = timestamp.toEpochSecond(ZoneOffset.UTC);
        redisTemplate.opsForZSet()
                .addIfAbsent(feedKey, postId.toString(), score);
        redisTemplate.opsForZSet().removeRange(feedKey, 0, - feed_size - 1);
    }

    public List<Long> getFeedPosts(Long userId, Long lastPostId, int pageSize) {
        String feedKey = getFeedKey(userId);
        Set<String> posts;

        if (lastPostId == null) {
            posts = redisTemplate.opsForZSet().reverseRange(feedKey, 0, pageSize - 1);
        } else {
            Double score = redisTemplate.opsForZSet().score(feedKey, lastPostId.toString());
            if (score != null) {
                posts = redisTemplate.opsForZSet().reverseRangeByScore(
                        feedKey, Double.NEGATIVE_INFINITY, score, 0, pageSize);
            } else {
                posts = new LinkedHashSet<>();
            }
        }

        if (posts == null) {
            return Collections.emptyList();
        }

        return posts.stream()
                .filter(this::isValidLong)
                .map(Long::valueOf)
                .toList();
    }

    private boolean isValidLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            log.warn("Invalid post ID format in Redis: {}", str);
            return false;
        }
    }

    private String getFeedKey(Long userId) {
        return "feed:" + userId;
    }
}