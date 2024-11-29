package faang.school.postservice.repository.cache;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CacheRepository<T> {

    void set(String key, T value, Duration time);

    void multiSetIfAbsent(Map<String, T> keyByValue);

    long incrementAndGet(String key);

    Optional<T> get(String key, Class<T> clazz);

    Optional<List<T>> getAll(List<String> keys, Class<T> clazz);
}
