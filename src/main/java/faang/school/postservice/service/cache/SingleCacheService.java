package faang.school.postservice.service.cache;

public interface SingleCacheService<K, V> {

    void save(K key, V value);

    V get(K key);
}
