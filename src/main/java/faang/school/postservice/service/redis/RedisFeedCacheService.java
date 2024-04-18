package faang.school.postservice.service.redis;

import faang.school.postservice.dto.redis.PostFeedDto;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    public void addPostToFeed(long userId, PostFeedDto postFeedDto) {
        redisFeedRepository.findById(userId)
                .ifPresentOrElse(
                        (redisFeed) -> {
                            redisFeed.addPostFeedDto(postFeedDto);
                            redisTemplate.update(redisFeed);
                        },
                        () -> {
                            RedisFeed redisFeed = RedisFeed.builder()
                                    .userId(userId)
                                    .ttl(feedTtl)
                                    .postIds(new TreeSet<>(Set.of(postFeedDto)))
                                    .build();
                            redisFeedRepository.save(redisFeed);
                        }
                );
    }

    @Retryable(retryFor = {EntityNotFoundException.class}, maxAttempts = 5, backoff =
    @Backoff(delay = 500, multiplier = 3), recover = "recoverRemovePostFromFeed")
    public void removePostFromFeed(long userId, PostFeedDto postFeedDto) {
        RedisFeed redisFeed = redisFeedRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("There is no post in feed to delete"));

        redisFeed.removePost(postFeedDto);
        redisTemplate.update(redisFeed);
    }

    @Recover
    private void recoverRemovePostFromFeed(EntityNotFoundException ex, long userId, PostFeedDto postFeedDto) {
        log.error("There was attempt to remove non-existing post {} from user feed {}", userId, postFeedDto, ex);
    }
}
