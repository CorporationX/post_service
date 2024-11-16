package faang.school.postservice.repository;

import faang.school.postservice.config.CachePostProperties;
import faang.school.postservice.service.cache.ListCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CachePostRepository implements CacheRepository<Long> {

    private final ListCacheService<Long> listCacheService;
    private final CachePostProperties cachePostProperties;

    @Override
    public void save(String userId, Long postId) {
        Runnable runnable = () -> {
            Duration timeToLive = Duration.ofHours(cachePostProperties.getCountHoursTimeToLive());
            listCacheService.put(userId, postId, timeToLive);

            if (listCacheService.size(userId) > cachePostProperties.getNewsFeedSize()) {
                listCacheService.leftPop(userId, Long.class);
            }
        };
        listCacheService.runInOptimisticLock(runnable, userId);
    }
}
