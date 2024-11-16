package faang.school.postservice.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.RedisTransactionException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisListCacheService<T> implements ListCacheService<T> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void put(String key, T value, Duration timeToLive) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public long size(String key) {
        Long size = redisTemplate.opsForList().size(key);
        return Objects.requireNonNullElse(size, 0L);
    }

    @Override
    @Retryable(retryFor = RedisTransactionException.class,
            maxAttemptsExpression = "${spring.data.redis.transaction.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${spring.data.redis.transaction.retry.backoff.delay}"))
    public void runInOptimisticLock(Runnable task, String key) {
        var operation = new SessionCallback<>() {
            public List<Object> execute(RedisOperations operations) {
                try {
                    operations.multi();
                    task.run();
                    return operations.exec();
                } catch (RuntimeException exception) {
                    operations.discard();
                    throw new RedisTransactionException();
                }
            }
        };
        redisTemplate.execute(operation);
    }

    @Override
    public Optional<T> leftPop(String listKey, Class<T> clazz) {
        Object leftValue = redisTemplate.opsForList().leftPop(listKey);
        return Optional.ofNullable(leftValue)
                .map(value -> objectMapper.convertValue(value, clazz));
    }
}
