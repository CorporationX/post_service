package faang.school.postservice.redis.service;

import faang.school.postservice.redis.model.entity.FeedCache;

import java.util.concurrent.CompletableFuture;

public interface FeedCacheService {

    CompletableFuture<Void> getAndSaveFeed(Long feedId, Long postId);

    void savePreparedFeed(FeedCache feedCache);
}
