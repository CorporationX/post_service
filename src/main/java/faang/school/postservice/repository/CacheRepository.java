package faang.school.postservice.repository;

public interface CacheRepository<T> {

    void save(String collectionName, T value);
}
