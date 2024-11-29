package faang.school.postservice.repository.cache;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SortedSetCacheRepository<T> {

    void put(String key, T value, double score);

    Optional<T> popMin(String sortedSetKey);

    long size(String key);

    void executeInOptimisticLock(Runnable task, String key);

    List<T> getRange(String key, String startValueKey, int offset, int count, Class<T> clazz);

    Set<T> get(String key);
}
