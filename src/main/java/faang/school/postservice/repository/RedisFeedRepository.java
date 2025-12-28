package faang.school.postservice.repository;

import faang.school.postservice.dto.post.PostV2Dto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class RedisFeedRepository {
    private static final String FEED_KEY_PREFIX = "feed:";
    private static final String POSTS_KEY_PREFIX = "post_";

    private final RedisTemplate<String, Object> redisTemplateFeed;
    private final RedisTemplate<String, PostV2Dto> redisTemplatePosts;

    public RedisFeedRepository(@Qualifier("redisTemplateFeed") RedisTemplate<String, Object> redisTemplateFeed,
                               @Qualifier("redisTemplatePost") RedisTemplate<String, PostV2Dto> redisTemplatePosts) {
        this.redisTemplateFeed = redisTemplateFeed;
        this.redisTemplatePosts = redisTemplatePosts;
    }

    public void addToUserFeed(long userId, Long postId, double score) {
        String key = FEED_KEY_PREFIX + userId;
        redisTemplateFeed.opsForZSet().add(key, postId.toString(), score);
    }

    public List<String> getUserFeed(long userId, String lastPostId, int limit) {
        String key = FEED_KEY_PREFIX + userId;
        ZSetOperations<String, Object> zSetOps = redisTemplateFeed.opsForZSet();

        if (lastPostId == null) {
            Set<Object> postIds = zSetOps.reverseRange(key, 0, limit - 1);
            return postIds.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }

        Double lastScore = zSetOps.score(key, lastPostId);
        if (lastScore == null) {
            Set<Object> postIds = zSetOps.reverseRange(key, 0, limit - 1);
            return postIds.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }

        Set<ZSetOperations.TypedTuple<Object>> tuples = zSetOps.reverseRangeByScoreWithScores(
                key, Double.NEGATIVE_INFINITY, lastScore - 0.1, 0, limit);

        return tuples.stream()
                .map(t -> t.getValue().toString())
                .filter(id -> !id.equals(lastPostId))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public PostV2Dto getPostFromRedis(Long postId) {
        String key = POSTS_KEY_PREFIX + postId;
        return redisTemplatePosts.opsForValue().get(key);
    }

    public List<PostV2Dto> getPostsFromRedis(List<Long> postIds) {
        return postIds.stream()
                .map(this::getPostFromRedis)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public long getUserFeedSize(long userId) {
        String key = FEED_KEY_PREFIX + userId;
        Long size = redisTemplateFeed.opsForZSet().size(key);
        return size != null ? size : 0;
    }
}
