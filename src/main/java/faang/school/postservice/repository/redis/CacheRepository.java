package faang.school.postservice.repository.redis;

public interface CacheRepository<T> {

    void save(T t);

    T get(long keyId);
}
