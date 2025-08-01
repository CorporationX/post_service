package faang.school.postservice.repository;

public interface RedisAuthorCache {
    void save(String key, Object value);
}
