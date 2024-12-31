package faang.school.postservice.redis.service;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.event.FeedDto;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheService postCacheService;
    private final PostCacheMapper postCacheMapper;
    private final RedisProperties redisProperties;

    public void addPostIdToAuthorFollowers(Long postId, List<Long> followerIds, LocalDateTime publishedAt) {
        followerIds.forEach(followerId -> addPostIdToFollowerFeed(postId, followerId, publishedAt));
    }

    public List<PostDto> getFeedByUserId(Long userId, Long postId) {
        List<Long> followerPostIds = getFollowerPostIds(userId, postId);

        return postCacheService.getPostCacheByIds(followerPostIds).stream()
                .map(postCacheMapper::toDto)
                .toList();
    }

    public void saveUserFeedHeat(FeedDto feedDto) {
        String feedCacheKey = generateFeedCacheKey(feedDto.followerId());

        feedDto.posts().forEach(post -> {
            var score = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            redisTemplate.opsForZSet().add(feedCacheKey, post, score);
        });
    }

    private List<Long> getFollowerPostIds(Long userId, Long postId) {
        Integer batchSize = redisProperties.getFeedCacheBatchSize();
        String feedCacheKey = generateFeedCacheKey(userId);

        if (postId == null) {
            // Если postId не указан, получаем первые batchSize постов
            return getFeedInRange(feedCacheKey, 0, batchSize - 1);
        } else {
            // Получаем позицию поста в ZSet
            Long rank = redisTemplate.opsForZSet().rank(feedCacheKey, postId);

            if (rank == null) {
                // Если пост не найден в ZSet, возвращаем первые batchSize постов
                return getFeedInRange(feedCacheKey, 0, batchSize - 1);
            }

            // Вычисляем диапазон для извлечения
            int start = Math.toIntExact(rank + 1); // rank + 1
            int end = Math.toIntExact(rank + batchSize); // rank + batchSize

            // Убедитесь, что end не выходит за пределы максимального размера
            // (например, если в ZSet меньше элементов, чем batchSize)
            Long zCard = redisTemplate.opsForZSet().zCard(feedCacheKey);
            if (zCard != null && end > zCard) {
                end = Math.toIntExact(zCard);
            }

            return getFeedInRange(feedCacheKey, start, end);
        }
    }

    private List<Long> getFeedInRange(String feedCacheKey, long startPostId, long endPostId) {
        Set<Object> result = redisTemplate.opsForZSet().range(feedCacheKey, startPostId, endPostId);

        if (result == null) {
            return emptyList();
        }

        return result.stream()
                .map(obj -> (Long) obj)
                .toList();
    }

    private void addPostIdToFollowerFeed(Long postId, Long followerId, LocalDateTime publishedAt) {
        Integer maxFeedSize = redisProperties.getMaxFeedSize();
        String feedCacheKey = generateFeedCacheKey(followerId);
        long score = publishedAt.toInstant(ZoneOffset.UTC).toEpochMilli();

        redisTemplate.opsForZSet().add(feedCacheKey, postId, score);

        Long setSize = redisTemplate.opsForZSet().zCard(feedCacheKey);
        if (setSize != null && setSize > maxFeedSize) {
            redisTemplate.opsForZSet().removeRange(feedCacheKey, 0, setSize - maxFeedSize);
        }
    }

    private String generateFeedCacheKey(Long followerId) {
        String feedCacheKeyPrefix = redisProperties.getFeedCacheKeyPrefix();
        return feedCacheKeyPrefix + followerId;
    }
}