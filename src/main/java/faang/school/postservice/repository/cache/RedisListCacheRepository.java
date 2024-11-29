package faang.school.postservice.repository.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisListCacheRepository<T> implements ListCacheRepository<T> {

    private final RedisTemplate<String, T> redisTemplate;

    @Override
    public void rightPush(String listKey, T value) {
        redisTemplate.opsForList().rightPush(listKey, value);
    }

    @Override
    public List<T> get(String listKey) {
        return redisTemplate.opsForList().range(listKey, 0, -1);
    }
}
