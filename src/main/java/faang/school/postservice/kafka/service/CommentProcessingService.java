package faang.school.postservice.kafka.service;

import faang.school.postservice.kafka.event.CommentEvent;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.service.redis.dto.CommentCacheDto;
import faang.school.postservice.service.redis.entity.PostRedis;
import faang.school.postservice.service.redis.repository.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentProcessingService {

    private final PostRedisRepository postRedisRepository;
    private final CommentEventMapper commentEventMapper;

    @Value("${app.redis.post.max-comments}")
    private int maxCommentsInCache;

    @Retryable(
            value = {OptimisticLockingFailureException.class, RedisConnectionFailureException.class},
            maxAttemptsExpression = "${app.redis.post.retry}",
            backoff = @Backoff(delayExpression = "${app.redis.post.backoff-delay:1000}")
    )
    public void addCommentToCache(CommentEvent event) {
        Optional<PostRedis> postOptional = postRedisRepository.findById(event.getPostId());

        if (postOptional.isEmpty()) {
            log.warn("Post with ID {} not found in cache. Skipping comment event.", event.getPostId());
            return;
        }

        PostRedis postRedis = postOptional.get();

        CommentCacheDto dto = commentEventMapper.createDtoFromEvent(event);

        postRedis.getLastComments().add(dto);

        while (postRedis.getLastComments().size() > maxCommentsInCache) {
            postRedis.getLastComments().pollFirst();
        }

        postRedisRepository.save(postRedis);
    }

    @Recover
    public void recover(Throwable ex, CommentEvent event) {
        log.error("Неожиданная ошибка при обработке события {}: {}", event, ex.getMessage(), ex);
        throw new RuntimeException(ex);
    }
}
