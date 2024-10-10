package faang.school.postservice.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisTransactionsService {

    private final RedisTemplate<String, Long> feedRedisTemplate;

    public <T, M> void implementOperation(T key, M value, int maxAttempts, BiConsumer<RedisOperations<T, M>, T> consumer) {
        int attempt = 0;
        List<Object> transactionResult;
        while (attempt < maxAttempts) {
            attempt++;
            try {
                transactionResult = feedRedisTemplate.execute(new SessionCallback<List<Object>>() {
                    @Override
                    public List<Object> execute(@NonNull RedisOperations operations) throws DataAccessException {

                        operations.watch(key);
                        operations.multi();
                        consumer.accept(operations, key);
                        return operations.exec();
                    }
                });

                if (transactionResult != null) {
                    log.info("Transaction of adding post was fulfilled successfully");
                    break;
                } else {
                    log.info("Transaction of adding post was not fulfilled successfully");
                    if (attempt >= maxAttempts) {
                        log.warn("Max attempts reached, attempt: {} so adding post will not be implemented", attempt);
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        log.error("Sleep interrupted", e);
                    }
                }
            } catch (DataAccessException e) {
                log.error("Transaction of adding post was not fulfilled successfully due to: {}", e.getMessage(), e);
            }
        }
    }
}
