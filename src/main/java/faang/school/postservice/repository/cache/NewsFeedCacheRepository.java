package faang.school.postservice.repository.cache;

import faang.school.postservice.model.redis.NewsFeedRedis;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NewsFeedCacheRepository {
    private final RedisCommands<String, String> redisCommands;

    @Value("${spring.data.redis.feed.max-feed-size}")
    private int maxSize;

    @Value("${spring.data.redis.feed.days-in-feed}")
    private long daysInCache;

    @Value("${spring.data.redis.feed.prefix}")
    private String prefix;

    public Optional<NewsFeedRedis> findById(long userId){
        String key = prefix + userId;
        List<String> values = redisCommands.zrange(key,0,-1);
        if (values.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new NewsFeedRedis(key, values));
    }

    @Retryable(retryFor = RuntimeException.class,
    maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void savePostToFeed(long userId, Long postId, LocalDateTime createdAt) {
        String key = prefix + userId;
        redisCommands.watch(key);
        boolean keyExists = existsById(key);
        try {
            double score = 1.0* createdAt.toEpochSecond(ZoneOffset.UTC);
            if (keyExists) {
                long count = redisCommands.zcard(key);
                redisCommands.multi();
                if (count < maxSize) {
                    redisCommands.zadd(key, score, "post" + postId);
                } else {
                    redisCommands.zpopmin(key);
                    redisCommands.zadd(key, score, "post" + postId);
                }
            } else {
                redisCommands.multi();
                redisCommands.zadd(key, score, "post" + postId);
                redisCommands.expire(key, 3600 * 24 * daysInCache);
            }
            TransactionResult result =  redisCommands.exec();
            checkTransactionResult(result);
        } catch (Exception e) {
            redisCommands.discard();
            throw new RuntimeException("Couldn't save post into news Feed");
        } finally {
            redisCommands.unwatch();
        }
    }

    @Retryable(retryFor = RuntimeException.class,
            maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public List<String> popPostsFromFeed(long userId, long count) {
        String key = prefix + userId;

        List<ScoredValue<String>> post;
        redisCommands.watch(key);
        try {
            post = redisCommands.zpopmax(key, count);
        } catch (Exception e) {
            redisCommands.discard();
            throw new RuntimeException("Couldn't get posts from news Feed");
        } finally {
            redisCommands.unwatch();
        }
        return post.stream().map(ScoredValue::getValue).toList();
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

    public boolean existsById(String userIdKey) {
        return redisCommands.exists(userIdKey) > 0;
    }
}



