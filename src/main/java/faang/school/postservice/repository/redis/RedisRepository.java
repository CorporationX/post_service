package faang.school.postservice.repository.redis;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public interface RedisRepository<K, V, D> {

    void save(D d);

    default void setTtl(RedisTemplate<K, V> redisTemplate, K key, Long ttl) {
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }
}
