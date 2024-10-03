package faang.school.postservice.repository.cache;

import faang.school.postservice.validator.feed.FeedValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FeedCacheRepository {

    private static final String CACHE_PREFIX = "feed:";
    private static final String RECOMMENDATION_PREFIX = "recommendation:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedValidator feedValidator;

    @Value("${feed.max-size}")
    private int maxFeedSize;

    @Value("${feed.recommendation-size}")
    private int recommendationSize;

    public void update(Long subscriberId, Long postId) {
        String feedKey = CACHE_PREFIX + subscriberId;

        Set<Object> feedPosts = redisTemplate.opsForZSet().
                reverseRange(feedKey, 0, maxFeedSize - 1);

        if (feedPosts != null && feedPosts.size() >= maxFeedSize) {
            Object oldestPost = redisTemplate.opsForZSet().range(feedKey, 0, 0).iterator().next();
            redisTemplate.opsForZSet().remove(feedKey, oldestPost);
        }

        redisTemplate.opsForZSet().add(feedKey, postId, System.currentTimeMillis());
    }

    public List<Long> getTopPosts(Long subscriberId, int batchSize, long postId) {
        feedValidator.validateMaxFeedSize(batchSize);

        String feedKey = CACHE_PREFIX + subscriberId;

        Set<Object> topPosts = redisTemplate.opsForZSet().reverseRange(feedKey, postId, batchSize - 1);

        if (topPosts != null && !topPosts.isEmpty()) {
            return topPosts.stream()
                    .map(post -> {
                        if (post instanceof Integer) {
                            return ((Integer) post).longValue();
                        } else {
                            return (Long) post;
                        }
                    })
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    public void updateRecommendation(List<Long> recommendedPostIds) {
        String recommendationsKey = RECOMMENDATION_PREFIX;

        redisTemplate.delete(recommendationsKey);

        long timestamp = System.currentTimeMillis();
        for (Long postId : recommendedPostIds) {
            redisTemplate.opsForZSet().add(recommendationsKey, postId, timestamp++);
        }

        redisTemplate.opsForZSet().removeRange(recommendationsKey, 0, -(recommendationSize + 1));
    }

    public List<Long> getRecommendation(int batchSize) {
        String recommendationsKey = RECOMMENDATION_PREFIX;

        Set<Object> topPosts = redisTemplate.opsForZSet().reverseRange(recommendationsKey, 0, batchSize - 1);

        if (topPosts != null && !topPosts.isEmpty()) {
            return topPosts.stream()
                    .map(post -> {
                        if (post instanceof Integer) {
                            return ((Integer) post).longValue();
                        } else {
                            return (Long) post;
                        }
                    })
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    public List<Long> getUniqueRecommendation(List<Long> feed, int batchSize) {
        String recommendationsKey = RECOMMENDATION_PREFIX;

        Set<Object> topPosts = redisTemplate.opsForZSet().reverseRange(recommendationsKey, 0, -1);

        if (topPosts != null && !topPosts.isEmpty()) {
            List<Long> recommendedPostIds = topPosts.stream()
                    .map(post -> (post instanceof Integer) ? ((Integer) post).longValue() : (Long) post)
                    .collect(Collectors.toList());

            List<Long> uniqueRecommendations = recommendedPostIds.stream()
                    .filter(postId -> !feed.contains(postId))
                    .limit(batchSize)
                    .collect(Collectors.toList());

            return uniqueRecommendations;
        }

        return List.of();
    }


}
