package faang.school.postservice.service.cache;

import java.time.Duration;
import java.util.Optional;

public interface ListCacheService<T> {

    void put(String listKey, T value, Duration timeToLive);

    Optional<T> leftPop(String listKey, Class<T> clazz);

    long size(String key);

    void runInOptimisticLock(Runnable task, String key);
}
