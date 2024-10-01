package faang.school.postservice.repository.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public abstract class AbstractRedisRepository<T> {
    protected final String cachePrefix;
    protected final RedisTemplate<String, Object> redisTemplate;
    private final long timeToLive;

    public void save(long id, T dto) {
        String key = cachePrefix + id;
        redisTemplate.opsForValue()
                .set(key, dto, timeToLive, TimeUnit.SECONDS);
    }

    public Optional<T> get(long id) {
        String key = cachePrefix + id;
        return Optional.ofNullable((T) redisTemplate.opsForValue().get(key));
    }

    public abstract void saveAll(List<T> dto);
}
