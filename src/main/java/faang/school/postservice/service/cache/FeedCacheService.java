package faang.school.postservice.service.cache;

import faang.school.postservice.config.props.FeedProps;
import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCacheService {
    private static final String KEY_SET_PATTERN = "%s:%d:%s";

    private final FeedProps feedProps;
    private final RedisTemplate<String, Long> redisTemplate;

    public void addPost(long postId, LocalDateTime publishedAt, List<Long> followees) {
        followees.forEach(userId -> {
            String key = KEY_SET_PATTERN.formatted(feedProps.key(), userId, feedProps.keySet());
            redisTemplate.opsForZSet().add(key, postId, publishedAt.toEpochSecond(ZoneOffset.UTC));
            trimFeedIfNecessary(key);
            redisTemplate.expire(key, Duration.ofSeconds(feedProps.ttl()));
            log.info("Added postId = {} to followee with id = {} feed", userId, postId);
        });
        log.info("Post with id = {} added to all followee feeds", postId);
    }

    public void addPosts(long userId, List<PostDto> posts) {
        String key = KEY_SET_PATTERN.formatted(feedProps.key(), userId, feedProps.keySet());

        posts.forEach(post -> {
            redisTemplate.opsForZSet().add(key, post.id(), post.publishedAt().toEpochSecond(ZoneOffset.UTC));
            trimFeedIfNecessary(key);
            redisTemplate.expire(key, Duration.ofSeconds(feedProps.ttl()));
            log.info("Add postId = {} to userId = {} feed", post.id(), userId);
        });
        log.info("{} posts added to userId = {} feed", posts.size(), userId);
    }

    public Set<Long> getNextPosts(long userId, Long lastPostId, int batch) {
        String key = KEY_SET_PATTERN.formatted(feedProps.key(), userId, feedProps.keySet());
        ZSetOperations<String, Long> redisSet = redisTemplate.opsForZSet();
        if (lastPostId == null) {
            return redisSet.reverseRange(key, 0, batch - 1);
        } else {
            Long rank = redisSet.reverseRank(key, lastPostId);
            if (rank == null) {
                return Collections.emptySet();
            }
            rank++;
            return redisSet.reverseRange(key, rank, rank + batch - 1);
        }
    }

    private void trimFeedIfNecessary(String key) {
        Long feedSize = redisTemplate.opsForZSet().size(key);
        if (feedSize != null && feedSize > feedProps.maxStored()) {
            redisTemplate.opsForZSet().removeRange(key, 0, feedSize - feedProps.maxStored() - 1);
            log.info("Removed posts from feed {}. Cause: max storage limit is exceeded", key);
        }
    }
}
