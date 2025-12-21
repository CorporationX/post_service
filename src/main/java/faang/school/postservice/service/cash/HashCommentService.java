package faang.school.postservice.service.cash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCommentService {

    @Value("${spring.data.redis.post-repository.ttl-days:1}")
    private long ttlDays;

    @Value("${spring.data.redis.post-repository.max-comments:3}")
    private int maxComments;

    private static final String POST_COMMENTS_ZSET_PREFIX = "post:%s:comments";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttemptsExpression = "${spring.data.redis.post-repository.retry.max-attempts:3}",
            backoff = @Backoff(delayExpression = "${spring.data.redis.post-repository.retry.delay:500}")
    )
    public void addComment(CommentEvent commentEvent) {
        String zKey = getCommentsZSetKey(commentEvent.postId());

        redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            connection.watch(zKey.getBytes());

            try {
                connection.multi();
                connection.zAdd(zKey.getBytes(),
                        getScore(commentEvent.createdAt()),
                        objectMapper.writeValueAsBytes(commentEvent));
                List<Object> execResults = connection.exec();

                if (execResults.isEmpty()) {
                    log.error("Optimistic locking failure");
                    throw new OptimisticLockingFailureException("Optimistic locking failure");
                }

            } catch (JsonProcessingException | DataAccessException e) {
                connection.discard();
                log.error("Error while adding comment to redis", e);
                throw new RuntimeException(e);
            } catch (Exception e) {
                connection.discard();
                throw e;
            }
            return true;
        });

        removeOldComments(zKey);
    }

    private void removeOldComments(String zKey) {
        long countComments = Optional.ofNullable(redisTemplate.opsForZSet().zCard(zKey)).orElse(0L);
        if (countComments > maxComments) {
            log.info("Removing old comments for post {}", zKey);
            redisTemplate.opsForZSet().removeRange(zKey, 0, -maxComments - 1);
        }
    }

    private String getCommentsZSetKey(Long postId) {
        return String.format(POST_COMMENTS_ZSET_PREFIX, postId);
    }

    private Double getScore(LocalDateTime dateTime) {
        double score;
        if (dateTime == null) {
            score = System.currentTimeMillis();
        } else {
            score = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        }
        return score;
    }
}