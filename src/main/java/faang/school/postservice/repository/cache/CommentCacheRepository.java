package faang.school.postservice.repository.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.event.comment.CommentKafkaEvent;
import faang.school.postservice.model.redis.CommentRedis;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentCacheRepository {
    private final ObjectMapper objectMapper;
    private final RedisCommands<String, String> redisCommands;

    @Value("${spring.data.redis.comment.max-size}")
    private int maxSize;

    @Value("${spring.data.redis.comment.days-in-feed}")
    private long daysInCache;

    @Value("${spring.data.redis.comment.prefix}")
    private String prefix;

    @Retryable(retryFor = RuntimeException.class,
            maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void save(CommentKafkaEvent commentKafkaEvent) {
        String key = prefix + commentKafkaEvent.getPostId();
        redisCommands.watch(key);
        boolean keyExists = existsById(key);
        try {
            double score = 1.0 * commentKafkaEvent.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
            String value = objectMapper.writeValueAsString(commentKafkaEvent);
            if (keyExists) {
                long count = redisCommands.zcard(key);
                redisCommands.multi();
                if (count < maxSize) {
                    redisCommands.zadd(key, score, value);
                } else {
                    redisCommands.zpopmin(key);
                    redisCommands.zadd(key, score, value);
                }
            } else {
                redisCommands.multi();
                redisCommands.zadd(key, score,value);
                redisCommands.expire(key, 3600 * 24 * daysInCache);
            }
            TransactionResult result =  redisCommands.exec();
            checkTransactionResult(result);
        } catch (Exception e) {
            redisCommands.discard();
            throw new RuntimeException("Couldn't save comment into comment Feed");
        } finally {
            redisCommands.unwatch();
        }
    }

    public Optional<CommentRedis> findById(long commentId){
        String key = prefix + commentId;
        List<String> values = redisCommands.zrange(key,0,-1);
        if (values.isEmpty()) {
            return Optional.empty();
        }
        List<CommentDto> valuesDto = objectMapper.convertValue(values, new TypeReference<List<CommentDto>>(){});
        return Optional.of(new CommentRedis(key, valuesDto));
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
    

    public boolean existsById(String postId) {
        return redisCommands.exists(postId) > 0;
    }
}
