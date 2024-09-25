package faang.school.postservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class AbstractCache {

    protected final RedisTemplate<String, Object> redisTemplate;

    public Boolean executeTransactionalOperation(String key, RedisCallback<Boolean> callback) {
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            connection.watch(key.getBytes());
            connection.multi();
            Boolean result = callback.doInRedis(connection);
            connection.exec();
            return result;
        });
    }
}
