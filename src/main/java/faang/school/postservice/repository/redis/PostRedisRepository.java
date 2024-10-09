package faang.school.postservice.repository.redis;

import faang.school.postservice.exception.RedisCommandExecutionException;
import faang.school.postservice.model.redis.PostRedis;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisSetCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRedisRepository {

    private final RedisAsyncCommands<String, String> redisAsyncCommands;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisRepositoryHelper<PostRedis> redisRepositoryHelper;
    @Value("${spring.data.redis.ttl.post}")
    private long ttlInSeconds;
    @Value("${spring.data.redis.fields.like}")
    private String likes;
    @Value("${spring.data.redis.fields.view}")
    private String views;
    private String postKey;

    @Retryable(
            retryFor = RedisCommandExecutionException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2.0))
    public void savePost(PostRedis postRedis) {

        postKey = "post:" + postRedis.getId();

        redisRepositoryHelper.executeTransaction(commands -> {
            commands.sadd("posts:author:" + postRedis.getAuthorId(), postKey);
            commands.hmset(postKey, redisRepositoryHelper.convertToMap(postRedis));
            commands.expire(postKey, ttlInSeconds);
        }, "posts", "Method savePost, transaction discard");
    }

    @Retryable(
            retryFor = RedisCommandExecutionException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2.0))
    public void addComment(String postId, String zsetKey) {
        RedisCommands<String, String> commandRead = connection.sync();

        String commentFieldValue = commandRead.hget(postId, "commentSetId");
        if (commentFieldValue == null || commentFieldValue.isEmpty()) {
            redisRepositoryHelper.executeTransaction((commands) ->
                            commands.hset(postId, "commentSetId", zsetKey),
                    postId, "Method addComment, transaction discard");
        }
    }

    @Retryable(
            retryFor = RedisCommandExecutionException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2.0))
    public void addLike(String postId) {
        redisRepositoryHelper.activityCounter(postId, likes);
    }

    @Retryable(
            retryFor = RedisCommandExecutionException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2.0))
    public void addView(String postId) {
        redisRepositoryHelper.activityCounter(postId, views);
    }
//    @Retryable(
//            retryFor = RedisCommandExecutionException.class,
//            maxAttempts = 5,
//            backoff = @Backoff(delay = 2000, multiplier = 2.0))
//    public void savePost(PostRedis postRedis) {
//
//        postKey = "post:" + postRedis.getId();
//
//        redisRepositoryHelper.executeTransaction(() -> {
//            redisAsyncCommands.sadd("posts:author:" + postRedis.getAuthorId(), postKey);
//            redisAsyncCommands.hmset(postKey, redisRepositoryHelper.convertToMap(postRedis));
//            redisAsyncCommands.expire(postKey, ttlInSeconds);
//        }, "posts", "Method savePost, transaction discard");
//    }
//
//    @Retryable(
//            retryFor = RedisCommandExecutionException.class,
//            maxAttempts = 5,
//            backoff = @Backoff(delay = 2000, multiplier = 2.0))
//    public void addComment(String postId, String zsetKey) {
//
//        redisAsyncCommands.hget(postId, "commentSetId").thenAccept(commentFieldValue -> {
//            if (commentFieldValue == null || commentFieldValue.isEmpty()) {
//                redisRepositoryHelper.executeTransaction(() ->
//                                redisAsyncCommands.hset(postId, "commentSetId", zsetKey),
//                        postId, "Method addComment, transaction discard");
//            }
//        });
//    }
//
//    @Retryable(
//            retryFor = RedisCommandExecutionException.class,
//            maxAttempts = 5,
//            backoff = @Backoff(delay = 2000, multiplier = 2.0))
//    public void addLike(String postId) {
//
//        redisRepositoryHelper.activityCounter(postId, likes);
//    }
//
//    @Retryable(
//            retryFor = RedisCommandExecutionException.class,
//            maxAttempts = 5,
//            backoff = @Backoff(delay = 2000, multiplier = 2.0))
//    public void addView(String postId) {
//
//        redisRepositoryHelper.activityCounter(postId, views);
//    }
}
