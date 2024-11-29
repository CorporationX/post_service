package faang.school.postservice.service.cache;

import java.util.List;

public interface MultiGetCacheService<K, V> {

    List<V> getAll(K key);
}
