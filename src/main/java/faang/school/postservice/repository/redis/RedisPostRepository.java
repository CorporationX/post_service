package faang.school.postservice.repository.redis;

import faang.school.postservice.config.properties.RetryProperties;
import faang.school.postservice.model.redis.CachedPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisPostRepository {
    private static final String POST_PREFIX = "post_";
    private final RedisTemplate<String, CachedPost> redisTemplate;
    private final RetryProperties retryProperties;

    @Value("${spring.data.redis.ttl-hours}")
    private long ttlHours;

    public void savePost(CachedPost post) {
       redisTemplate.opsForHash().putIfAbsent(POST_PREFIX + post.getId(), post.getId().toString(), post);
        redisTemplate.expire(POST_PREFIX + post.getId(), ttlHours, TimeUnit.HOURS);
        log.info("Saved post in Redis");
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class,
            maxAttemptsExpression = "#{@retryProperties.maxAttempts}",
            backoff = @Backoff(delayExpression = "#{@retryProperties.delay}"))
    public void updatePost(CachedPost post) {
        redisTemplate.opsForHash().put(POST_PREFIX + post.getId(), post.getId().toString(), post);
        redisTemplate.expire(POST_PREFIX + post.getId(), ttlHours, TimeUnit.HOURS);
        log.info("Updated post in Redis");
    }

    public CachedPost getPost(Long postId) {
        return (CachedPost) redisTemplate.opsForHash().get(POST_PREFIX + postId, postId.toString());
    }

    @Recover
    public void recover(OptimisticLockingFailureException ex, CachedPost cachedPost) {
        log.error("Failed to update post after multiple retries for id: {}", cachedPost.getId());
    }
}