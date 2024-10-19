package faang.school.postservice.repository.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.model.redis.UserRedis;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCacheRepository {
    @Value("${spring.data.redis.user.days-in-feed}")
    private long daysInCache;
    @Value("${spring.data.redis.user.prefix}")
    private String prefix;
    private final RedisCommands<String, String> redisCommands;
    private final ObjectMapper objectMapper;

    @Retryable(retryFor = RuntimeException.class,
            maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void save(UserRedis userRedis) {
        String key = prefix + userRedis.getId();
        boolean exists = existsById(key);
        redisCommands.watch(key);
        try {
            redisCommands.multi();
            if (exists) {
                redisCommands.expire(key, 3600 * 24 * daysInCache);
            } else {
                Map<String, String> values = objectMapper.convertValue(userRedis, new TypeReference<>(){});
                redisCommands.hset(key, values);
                redisCommands.expire(key, 3600 * 24 * daysInCache);
            }
            TransactionResult result = redisCommands.exec();
            checkTransactionResult(result);
        } catch (Exception e) {
            redisCommands.discard();
            throw new RuntimeException("Couldn't increment like value into post");
        } finally {
            redisCommands.unwatch();
        }
    }

    public Optional<UserRedis> findById(String userIdKey) {
        Object value = redisCommands.hgetall(userIdKey);
        return Optional.of(objectMapper.convertValue(value, UserRedis.class));
    }

    public boolean existsById(String userIdKey) {
        return redisCommands.exists(userIdKey) > 0;
    }
    public boolean existsById(Long userId) {
        return redisCommands.exists(prefix+ userId) > 0;
    }

    private void checkTransactionResult(TransactionResult result) {
        if (result == null) {
            throw new RuntimeException("Result of transactions is null");
        }
        for (Object res : result) {
            if (res == null) {
                throw  new RuntimeException("One of command in translation result have finished with null result");
            }
        }
    }
}
