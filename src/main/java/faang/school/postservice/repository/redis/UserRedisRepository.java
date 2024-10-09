package faang.school.postservice.repository.redis;

import faang.school.postservice.exception.RedisCommandExecutionException;
import faang.school.postservice.model.redis.UserRedis;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRedisRepository {
    private final RedisAsyncCommands<String, String> redisAsyncCommands;
    private final StatefulRedisConnection<String,String> connection;
    private final RedisRepositoryHelper<UserRedis> redisRepositoryHelper;
    @Value("${spring.data.redis.ttl.user}")
    private long ttlInSeconds;
    private String authorKey;

    @Retryable(
            retryFor = RedisCommandExecutionException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2.0))
    public void saveAuthor(UserRedis userRedis) {

        authorKey = "author:" + userRedis.getId();

        redisRepositoryHelper.executeTransaction((commands) -> {
            commands.sadd("authors", authorKey);
            commands.hmset(authorKey, redisRepositoryHelper.convertToMap(userRedis));
            commands.expire(authorKey, ttlInSeconds);
        }, "authors", "Method saveAuthor, transaction discard");
    }

//    @Retryable(
//            retryFor = RedisCommandExecutionException.class,
//            maxAttempts = 5,
//            backoff = @Backoff(delay = 2000, multiplier = 2.0))
//    public void saveAuthor(UserRedis userRedis) {
//        String authorKey = "author:" + userRedis.getId();
//
//        redisRepositoryHelper.executeTransaction(() -> {
//            redisAsyncCommands.sadd("authors", authorKey);
//            redisAsyncCommands.hmset(authorKey, redisRepositoryHelper.convertToMap(userRedis));
//            redisAsyncCommands.expire(authorKey, ttlInSeconds);
//        }, "authors", "Method saveAuthor, transaction discard");
//    }
}
