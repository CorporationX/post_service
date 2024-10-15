package faang.school.postservice.redis.cache.service.feed;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostCacheMapper;
import faang.school.postservice.redis.cache.service.post.PostCacheService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import static java.util.Collections.emptyList;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCacheServiceImpl implements FeedCacheService {

    @Value("${spring.data.redis.cache.settings.max-feed-size}")
    private long maxFeedSize;
    @Value("{spring.data.redis.lock-registry.feed.post-lock-key}")
    private String feedCacheKeyPrefix;
    @Value("{spring.data.redis.feed-cache.batch_size:20}")
    private int batchSize;

    private final ZSetOperations<String, Long> redisFeedZSetOps;
    private final RedisLockRegistry feedLockRegistry;
    private final PostCacheService postCacheService;
    private final PostCacheMapper postCacheMapper;

    @Override
    public void addPostToFeed(long postId, long subscriberId, LocalDateTime publishedAt) {
        String feedCacheKey = generateFeedCacheKey(subscriberId);
        long score = publishedAt.toInstant(ZoneOffset.UTC).toEpochMilli() * (-1);

        lock(() ->{
            redisFeedZSetOps.add(feedCacheKey, postId, score);

            Long setSize = redisFeedZSetOps.zCard(feedCacheKey);
            if (setSize != null && setSize > maxFeedSize) {
                redisFeedZSetOps.removeRange(feedCacheKey, 0, setSize - maxFeedSize);
            }
        }, feedCacheKey);
    }

    @Override
    public List<PostDto> getFeedByUserId(Long userId, Long postId){
        List<Long> followerPostIds = getFollowerPostIds(userId, postId);

        return postCacheService.getPostCacheByIds(followerPostIds).stream()
                .map(postCacheMapper::toDto)
                .toList();
    }

    private String generateFeedCacheKey(Long followerId) {
        return feedCacheKeyPrefix + followerId;
    }

    private List<Long> getFollowerPostIds(Long userId, Long postId) {
        String feedCacheKey = generateFeedCacheKey(userId);
        if (postId == null) {
            return getFeedInRange(feedCacheKey, 0, batchSize - 1);
        } else {
            Long rank = redisFeedZSetOps.rank(feedCacheKey, postId);

            if (rank == null) {
                return getFeedInRange(feedCacheKey, 0, batchSize - 1);
            }

            return getFeedInRange(feedCacheKey, rank + 1, rank + batchSize);
        }
    }

    private List<Long> getFeedInRange(String feedCacheKey, long rankStartPost, long rankEndPost) {

        Set<Long> result = redisFeedZSetOps.range(feedCacheKey, rankStartPost, rankEndPost);

        if (result == null) {
            return emptyList();
        }

        return result.stream().toList();
    }

    private void lock(Runnable operation, String lockKey) {

        Lock lock = feedLockRegistry.obtain(lockKey);

        if (lock.tryLock()) {
            try {
                operation.run();
            } finally {
                lock.unlock();
            }
        } else {
            throw new OptimisticLockException("Failed to obtain lock for key: " + lockKey);
        }
    }
}
