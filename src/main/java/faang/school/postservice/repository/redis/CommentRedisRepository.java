package faang.school.postservice.repository.redis;

import faang.school.postservice.exception.RedisCommandExecutionException;
import faang.school.postservice.model.redis.CommentRedis;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;

@Repository
@RequiredArgsConstructor
public class CommentRedisRepository  {
    private final RedisAsyncCommands<String, String> redisAsyncCommands;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisRepositoryHelper<CommentRedis> redisRepositoryHelper;
    @Value("${spring.data.redis.ttl.comment}")
    private long ttlInSeconds;
    private String commentKey;
    @Value("${spring.data.redis.fields.like}")
    private String likes;
    private long score;

    @Retryable(
            retryFor = RedisCommandExecutionException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2.0))
    public void saveComment(CommentRedis commentRedis, String zsetKey) {

        commentKey = "com:" + commentRedis.getId();
        score = commentRedis.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        redisRepositoryHelper.executeTransaction((commands) -> {
            commands.zadd(zsetKey, score, commentKey);
            commands.hmset(commentKey, redisRepositoryHelper.convertToMap(commentRedis));
            commands.expire(commentKey, ttlInSeconds);

            if (commands.zcard(zsetKey) > 3) {
                String oldestCommentKey = commands.zpopmin(zsetKey).getValue();
                if (oldestCommentKey != null) {
                    commands.del(oldestCommentKey);
                }
            }
        }, zsetKey, "Method saveComment, transaction discard");
    }

    @Retryable(
            retryFor = RedisCommandExecutionException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2.0))
    public void addLike(String commentId) {
        redisRepositoryHelper.activityCounter(commentId, likes);
    }
//    @Retryable(
//            retryFor = RedisCommandExecutionException.class,
//            maxAttempts = 5,
//            backoff = @Backoff(delay = 2000, multiplier = 2.0))
//    public void saveComment(CommentRedis commentRedis, String zsetKey) {
//
//        commentKey = "com:" + commentRedis.getId();
//        score = commentRedis.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
//
//        redisRepositoryHelper.executeTransaction(() -> {
//            redisAsyncCommands.zadd(zsetKey, score, commentKey);
//            redisAsyncCommands.hmset(commentKey, redisRepositoryHelper.convertToMap(commentRedis));
//            redisAsyncCommands.expire(commentKey, ttlInSeconds);
//
//            redisAsyncCommands.zcard(zsetKey).thenAccept(count -> {
//                if (count > 3) {
//                    redisAsyncCommands.zpopmin(zsetKey).thenAccept(removed -> {
//                        if (removed.hasValue()) {
//                            String oldestCommentKey = removed.getValue();
//                            redisAsyncCommands.del(oldestCommentKey);
//                        }
//                    });
//                }
//            });
//        }, zsetKey, "Method saveComment, transaction discard");
//    }
//
//    @Retryable(
//            retryFor = RedisCommandExecutionException.class,
//            maxAttempts = 5,
//            backoff = @Backoff(delay = 2000, multiplier = 2.0))
//    public void addLike(String commentId) {
//        redisRepositoryHelper.activityCounter(commentId,likes);
//    }
}
