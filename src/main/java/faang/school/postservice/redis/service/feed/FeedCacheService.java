package faang.school.postservice.redis.service.feed;

import java.util.concurrent.CompletableFuture;

public interface FeedCacheService {

    CompletableFuture<Void> getAndSaveFeed(Long feedId, Long postId);
}
