package faang.school.postservice.service.cache;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncCacheService<K, V> {

    CompletableFuture<V> save(K key, V value);

    CompletableFuture<List<V>> getRange(K key, K startValueKey, int count);
}
