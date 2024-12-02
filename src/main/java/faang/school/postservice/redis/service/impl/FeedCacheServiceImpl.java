package faang.school.postservice.redis.service.impl;

import faang.school.postservice.redis.model.entity.FeedCache;
import faang.school.postservice.redis.repository.FeedsCacheRepository;
import faang.school.postservice.redis.service.FeedCacheService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class FeedCacheServiceImpl implements FeedCacheService {
    private final FeedsCacheRepository feedsCacheRepository;
    private final RedissonClient redissonClient;

    @Value("${feed-posts.size}")
    private int feedSize;

    public FeedCacheServiceImpl(FeedsCacheRepository feedsCacheRepository, RedissonClient redissonClient) {
        this.feedsCacheRepository = feedsCacheRepository;
        this.redissonClient = redissonClient;
    }

    @Override
    @Async("feedExecutor")
    public CompletableFuture<Void> getAndSaveFeed(Long feedId, Long postId) {
        log.debug("Lock acquired for feedId: {}", postId);
        String lockKey = "lock:" + feedId;
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            FeedCache feedCache = feedsCacheRepository.findById(feedId)
                    .orElseGet(() -> new FeedCache(feedId, new LinkedList<>()));

            FeedCache newFeedCache = addPostIdToFeed(feedCache, postId);
            feedsCacheRepository.save(newFeedCache);
            log.info("Successfully added postId to feed : {}", feedId);
        } finally {
            lock.unlock();
            log.debug("Lock released for feedId: {}", feedId);
        }
        return CompletableFuture.completedFuture(null);
    }

    private FeedCache addPostIdToFeed(FeedCache feedCache, Long postId) {
        LinkedList<Long> restoredPostIds = feedCache.getPostIds();
        if (!restoredPostIds.contains(postId)) {
            restoredPostIds.add(0, postId);
        }
        if (restoredPostIds.size() > feedSize) {
            restoredPostIds.removeLast();
        }
        feedCache.setPostIds(restoredPostIds);
        return feedCache;
    }

    public void savePreparedFeed(FeedCache feedCache) {
        feedsCacheRepository.save(feedCache);
        log.info("Successfully saved feed for user with id {} : {} posts in total",
                feedCache.getId(), feedCache.getPostIds().size());
    }
}
