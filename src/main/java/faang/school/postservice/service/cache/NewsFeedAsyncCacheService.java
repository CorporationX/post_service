package faang.school.postservice.service.cache;

import faang.school.postservice.config.NewsFeedProperties;
import faang.school.postservice.repository.cache.SortedSetCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NewsFeedAsyncCacheService implements AsyncCacheService<Long, Long> {

    private final SortedSetCacheRepository<Long> sortedSetCacheRepository;
    private final NewsFeedProperties newsFeedProperties;

    @Override
    @Async("newsFeedThreadPoolExecutor")
    public CompletableFuture<Long> save(Long followerId, Long postId) {
        String followerFeedKey = createKey(followerId);
        Runnable runnable = () -> {
            sortedSetCacheRepository.put(followerFeedKey, postId, System.currentTimeMillis());

            if (sortedSetCacheRepository.size(followerFeedKey) >= newsFeedProperties.getNewsFeedSize()) {
                sortedSetCacheRepository.popMin(followerFeedKey);
            }
        };

        sortedSetCacheRepository.executeInOptimisticLock(runnable, followerFeedKey);
        return CompletableFuture.completedFuture(postId);
    }

    @Override
    @Async("newsFeedThreadPoolExecutor")
    public CompletableFuture<List<Long>> getRange(Long userId, Long startPostKey, int count) {
        String userKey = createKey(userId);
        List<Long> feed = sortedSetCacheRepository.getRange(userKey, Long.toString(startPostKey), 0, count, Long.class);
        return CompletableFuture.completedFuture(feed);
    }

    private String createKey(Long userId) {
        return userId + "::news_feed";
    }
}
