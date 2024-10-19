package faang.school.postservice.repository.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.redis.PostRedis;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostCacheRepository {
    private final ObjectMapper objectMapper;
    private final RedisCommands<String, String> redisCommands;

    @Value("${spring.data.redis.post.days-in-feed}")
    private long daysInCache;

    @Value("${spring.data.redis.post.prefix}")
    private String prefix;
    @Value("${spring.data.redis.comment.prefix}")
    private String prefixComment;
    @Retryable(retryFor = RuntimeException.class,
            maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void save(PostRedis postRedis) {
        String key = prefix + postRedis.getId();
        Map<String, String> values = objectMapper.convertValue(postRedis, new TypeReference<>(){});

        redisCommands.watch(key);
        try {
            redisCommands.multi();
            redisCommands.hset(key, values);
            redisCommands.expire(key, 3600 * 24 * daysInCache);
            TransactionResult result = redisCommands.exec();
            checkTransactionResult(result);
        } catch (Exception e) {
            redisCommands.discard();
            throw new RuntimeException("Couldn't save the post into cache");
        } finally {
            redisCommands.unwatch();
        }
    }

    public Optional<PostRedis> findById(String postId) {
        if (redisCommands.exists(postId)>0) {
            Object value = redisCommands.hgetall(postId);
            return Optional.of(objectMapper.convertValue(value, PostRedis.class));
        } else {
            return Optional.empty();
        }
    }

    public void incrementLikes(String postId) {
        incrementFieldInPost(postId,"likesCount", 1);
    }

    public void decrementLikes(String postId) {
        incrementFieldInPost(postId,"likesCount", -1);
    }

    public void incrementViews(String postId) {
        incrementFieldInPost(postId,"viewsCount", 1);
    }
    @Retryable(retryFor = RuntimeException.class,
            maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void addCommentsToPost(Long postId) {
        String postIdKey = prefix + postId;
        String commentIdKey = prefixComment + postId;
        if (!existsById(postId)) {
            throw new EntityNotFoundException("Couldn't find post in cache, postID = " + postId);
        }
        try {
            redisCommands.watch(postIdKey);
            redisCommands.multi();
            redisCommands.hset(postIdKey, "commentsKey", commentIdKey);
            TransactionResult result = redisCommands.exec();
            checkTransactionResult(result);
        } catch (Exception e) {
            redisCommands.discard();
            throw new RuntimeException("Couldn't increment like value into post");
        } finally {
            redisCommands.unwatch();
        }
    }

    @Retryable(retryFor = RuntimeException.class,
            maxAttempts = 5, backoff = @Backoff(delay = 1000))
    private void incrementFieldInPost(String postId, String field,int numberValues) {
        if (!existsById(postId)) {
            return;
        }
        try {
            redisCommands.watch(postId);
            redisCommands.multi();
            redisCommands.hincrby(postId, field, numberValues);
            redisCommands.expire(postId, 3600 * 24 * daysInCache);
            TransactionResult result = redisCommands.exec();
            checkTransactionResult(result);
        } catch (Exception e) {
            redisCommands.discard();
            throw new RuntimeException("Couldn't increment like value into post");
        } finally {
            redisCommands.unwatch();
        }
    }

    public boolean existsById(String keyPostId) {
        return redisCommands.exists(keyPostId) > 0;
    }

    public boolean existsById(Long postId) {
        return redisCommands.exists(prefix+postId) > 0;
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
