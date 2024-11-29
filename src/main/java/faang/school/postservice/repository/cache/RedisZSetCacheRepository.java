package faang.school.postservice.repository.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.RedisTransactionException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisZSetCacheRepository<T> implements SortedSetCacheRepository<T> {

    private final RedisTemplate<String, T> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void put(String key, T value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    @Override
    public long size(String key) {
        Long size = redisTemplate.opsForZSet().size(key);
        return Objects.requireNonNullElse(size, 0L);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Retryable(retryFor = RedisTransactionException.class,
            maxAttemptsExpression = "${spring.data.redis.transaction.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${spring.data.redis.transaction.retry.backoff.delay}"))
    public void executeInOptimisticLock(Runnable task, String key) {
        var operation = new SessionCallback<>() {
            public List<Object> execute(RedisOperations operations) {
                operations.watch(key);
                operations.multi();

                task.run();

                List<Object> result = operations.exec();
                operations.unwatch();

                if (result.isEmpty()) {
                    operations.discard();
                    throw new RedisTransactionException();
                }
                return result;
            }
        };

        redisTemplate.execute(operation);
    }

    @Override
    public List<T> getRange(String key, String startValueKey, int offset, int count, Class<T> clazz) {
        Double startValueScore = redisTemplate.opsForZSet().score(key, startValueKey);

        double score = Optional.ofNullable(startValueScore)
                .map(dScore -> dScore + 0.000001)
                .orElse(Double.NEGATIVE_INFINITY);
        Set<ZSetOperations.TypedTuple<T>> typedTuples =
                redisTemplate.opsForZSet().rangeByScoreWithScores(key, score, Double.MAX_VALUE, offset, count);

        return Optional.ofNullable(typedTuples)
                .map(typedTuples1 -> typedTuples1.stream()
                        .map(ZSetOperations.TypedTuple::getValue)
                        .map(value -> objectMapper.convertValue(value, clazz))
                        .toList())
                .orElse(Collections.emptyList());
    }

    @Override
    public Set<T> get(String key) {
        return redisTemplate.opsForZSet().range(key, 0, -1);
    }

    @Override
    public Optional<T> popMin(String sortedSetKey) {
        var tuple = redisTemplate.opsForZSet().popMin(sortedSetKey);
        return Optional.ofNullable(tuple)
                .map(ZSetOperations.TypedTuple::getValue);
    }
}
