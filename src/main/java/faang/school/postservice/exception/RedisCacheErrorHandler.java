package faang.school.postservice.exception;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

@Slf4j
public class RedisCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, @NonNull Cache cache,
                                    @NonNull Object key) {
        log.error("Cache GET error for key {}: {}", key, exception.getMessage());
    }

    @Override
    public void handleCachePutError(RuntimeException exception, @NonNull Cache cache,
                                    @NonNull Object key, Object value) {
        log.error("Cache PUT error for key {}: {}", key, exception.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, @NonNull Cache cache,
                                      @NonNull Object key) {
        log.error("Cache EVICT error for key {}: {}", key, exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, @NonNull Cache cache) {
        log.error("Cache CLEAR error: {}", exception.getMessage());
    }
}
