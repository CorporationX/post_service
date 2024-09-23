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

    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedValidator feedValidator;

    @Value("${feed.max-size}")
    private int maxFeedSize;

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

    /**
     *
     * Метод getTopPosts будет дорабатываться в момент написание эндпойнта для получения фида,
     * в соответствии с требованиями в тз. На данном этапе  я добавил его чтобы првоерить работает ли кэш
     */
    public List<Long> getTopPosts(Long subscriberId, int batchSize) {
        feedValidator.validateMaxFeedSize(batchSize);

        String feedKey = CACHE_PREFIX + subscriberId;

        Set<Object> topPosts = redisTemplate.opsForZSet().reverseRange(feedKey, 0, batchSize - 1);

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

}
