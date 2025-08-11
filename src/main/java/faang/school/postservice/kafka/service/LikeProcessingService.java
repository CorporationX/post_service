package faang.school.postservice.kafka.service;

import faang.school.postservice.kafka.event.LikeEvent;
import faang.school.postservice.service.redis.repository.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeProcessingService {

    private final PostRedisRepository postRedisRepository;

    @Retryable(
            value = {OptimisticLockingFailureException.class, RedisConnectionFailureException.class},
            maxAttemptsExpression = "${app.redis.post.retry}",
            backoff = @Backoff(delayExpression = "${app.redis.post.backoff-delay:1000}")
    )
    public void addLikeToCache(LikeEvent event) {
        postRedisRepository.findById(event.getPostId()).ifPresentOrElse(post -> {
            long current = post.getLikesCount() == null ? 0L : post.getLikesCount();
            long updated = current + 1L;
            post.setLikesCount(updated);
            postRedisRepository.save(post);
            log.debug("Like applied: postId={}, likes={}", event.getPostId(), updated);
        }, () -> {
            log.warn("Post with ID {} not found in cache. Skipping like event.", event.getPostId());
        });
    }

    @Recover
    public void recover(Throwable ex, LikeEvent event) {
        log.error("Failed to process like event for postId={}: {}", event.getPostId(), ex.getMessage(), ex);
        throw new RuntimeException(ex);
    }
}
