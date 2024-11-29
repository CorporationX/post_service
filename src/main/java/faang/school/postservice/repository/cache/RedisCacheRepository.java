package faang.school.postservice.repository.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisCacheRepository<T> implements CacheRepository<T> {

    private final RedisTemplate<String, T> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void set(String key, T value, Duration time) {
        redisTemplate.opsForValue().set(key, value, time);
    }

    @Override
    public void multiSetIfAbsent(Map<String, T> keyByValue) {
        redisTemplate.opsForValue().multiSetIfAbsent(keyByValue);
    }

    @Override
    public long incrementAndGet(String key) {
        Long counter = redisTemplate.opsForValue().increment(key);
        return Objects.requireNonNullElse(counter, 0L);
    }

    @Override
    public Optional<T> get(String key, Class<T> clazz) {
        T valueOrNull = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(valueOrNull)
                .map(value -> objectMapper.convertValue(value, clazz));
    }

    @Override
    public Optional<List<T>> getAll(List<String> keys, Class<T> clazz) {
        List<T> valueOrNull = redisTemplate.opsForValue().multiGet(keys);
        return Optional.ofNullable(valueOrNull)
                .map(values -> values.stream()
                        .map(value -> objectMapper.convertValue(value, clazz))
                        .toList()
                );
    }
}