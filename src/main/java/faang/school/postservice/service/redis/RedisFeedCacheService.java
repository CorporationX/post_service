package faang.school.postservice.service.redis;

import faang.school.postservice.dto.redis.PostIdDto;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisFeedCacheService {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisKeyValueTemplate redisTemplate;
    @Value("${spring.data.redis.cache.ttl.feed}")
    private int feedTtl;
    @Value("${feed.max_size}")
    private int feedBatchSize;

    @Retryable(retryFor = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff =
    @Backoff(delay = 300, multiplier = 3))
    public void savePostToFeed(long userId, PostIdDto postIdDto) {
        redisFeedRepository.findById(userId).ifPresentOrElse(
                (redisFeed) -> {
                    redisFeed.addPostIdDto(postIdDto);

                    if (redisFeed.getPostIds().size() > feedBatchSize) {
                        redisFeed.removeLastPostIdDto();
                    }

                    redisTemplate.update(redisFeed);
                },
                () -> {
                    RedisFeed redisFeed = RedisFeed.builder()
                            .userId(userId)
                            .ttl(feedTtl)
                            .postIds(new TreeSet<>(Set.of(postIdDto)))
                            .build();
                    redisFeedRepository.save(redisFeed);
                }
        );
    }

    @Retryable(retryFor = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff =
    @Backoff(delay = 300, multiplier = 3), recover = "recoverRemovePostFromFeed")
    public void removePostFromFeed(long userId, PostIdDto postIdDto) {
        RedisFeed redisFeed = getRedisFeed(userId);

        redisFeed.removePost(postIdDto);
        redisTemplate.update(redisFeed);
    }

    @Retryable(retryFor = {EntityNotFoundException.class}, maxAttempts = 5, backoff =
    @Backoff(delay = 500, multiplier = 10), recover = "recoverGetPostFromFeed")
    private RedisFeed getRedisFeed(long userId) {
        return redisFeedRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("There is no post in feed to delete"));
    }

    @Recover
    private void recoverGetPostFromFeed(EntityNotFoundException ex, long userId, PostIdDto postIdDto) {
        log.error("There was attempt to get non-existing post = {} from user's feed with userid = {}"
                , postIdDto, userId, ex);
    }
}