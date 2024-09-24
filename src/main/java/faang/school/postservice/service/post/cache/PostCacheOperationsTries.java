package faang.school.postservice.service.post.cache;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.exception.redis.RedisTransactionInterrupted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

import static faang.school.postservice.exception.redis.RedisErrorMessages.REDIS_TRANSACTION_INTERRUPTED;

@Slf4j
@EnableRetry
@RequiredArgsConstructor
@Service
public class PostCacheOperationsTries {

    @Value("${app.post.cache.number_of_top_in_cache}")
    private int numberOfTopInCache;

    private final RedisTemplate<String, PostCacheDto> redisTemplatePost;
    private final ZSetOperations<String, String> zSetOperations;

    @Retryable(
            retryFor = RedisTransactionInterrupted.class,
            maxAttemptsExpression = "${app.post.cache.retryable.save_keys.max_attempts}",
            backoff = @Backoff(
                    delayExpression = "${app.post.cache.retryable.save_keys.delay}",
                    multiplierExpression = "${app.post.cache.retryable.save_keys.multiplier}"
            )
    )
    public List<Object> tryToSaveChangesOfPost(PostCacheDto post, String postId, long timestamp, List<String> newTags,
                                               List<String> delTags, boolean toDeletePost) {
        redisTemplatePost.multi();
        log.info("Transaction started");

        if (toDeletePost) {
            redisTemplatePost.delete(postId);
        } else {
            redisTemplatePost.opsForValue().set(postId, post);
        }
        delTags.forEach(tag -> zSetOperations.remove(tag, postId));
        newTags.forEach(tag -> {
            zSetOperations.add(tag, postId, timestamp);
            zSetOperations.removeRange(tag, 0, (numberOfTopInCache + 1) * -1);
        });
        List<Object> resultOfExec = redisTemplatePost.exec();
        if (!resultOfExec.isEmpty()) {
            log.info("Transaction executed successfully");
        } else {
            redisTemplatePost.discard();
            log.info("Transaction discarded");
            throw new RedisTransactionInterrupted(REDIS_TRANSACTION_INTERRUPTED, post.getId());
        }
        return resultOfExec;
    }

    @Retryable(
            retryFor = RedisConnectionFailureException.class,
            maxAttemptsExpression = "${app.post.cache.retryable.compare_tags.max_attempts}",
            backoff = @Backoff(delayExpression = "${app.post.cache.retryable.compare_tags.delay}")
    )
    public List<String> tryFilterByTagsInCache(List<String> tags, String tagToFind)
            throws RedisConnectionFailureException {
        return tags
                .stream()
                .filter(tag -> tag.equals(tagToFind) | Boolean.TRUE.equals(redisTemplatePost.hasKey(tag)))
                .toList();
    }
}
