package faang.school.postservice.redis.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.kafka.events.FeedDto;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedCacheService {
    @Value("{spring.data.redis.feed-cache.size:500}")
    private int maxFeedSize;
    @Value("{spring.data.redis.feed-cache.key-prefix}")
    private String feedCacheKeyPrefix;
    @Value("{spring.data.redis.feed-cache.batch_size:20}")
    private int batchSize;

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheService postCacheService;
    private final PostCacheMapper postCacheMapper;

    public void addPostIdToAuthorFollowers(Long postId, List<Long> followerIds) {
        followerIds.forEach(followerId -> addPostIdToFollowerFeed(postId, followerId));
    }

    public List<PostDto> getFeedByUserId(Long userId, Long postId){
        var followerPostIds = getFollowerPostIds(userId, postId);

        return postCacheService.getPostCacheByIds(followerPostIds).stream()
                .map(postCacheMapper::toDto)
                .toList();
    }

    public void saveUserFeedHeat(FeedDto feedDto){
        var feedCacheKey = generateFeedCacheKey(feedDto.followerId());
        var score = currentTimeMillis();

        for (Long postId: feedDto.posts()){
            redisTemplate.opsForZSet().add(feedCacheKey, postId, score++);
        }
    }

    private List<Long> getFollowerPostIds(Long userId, Long postId) {
        var feedCacheKey = generateFeedCacheKey(userId);
        if (postId == null) {
            return getFeedInRange(feedCacheKey, 0, batchSize - 1);
        } else {
            var rank = redisTemplate.opsForZSet().rank(feedCacheKey, postId);

            if (rank == null) {
                return getFeedInRange(feedCacheKey, 0, batchSize - 1);
            }

            return getFeedInRange(feedCacheKey, rank + 1, rank + batchSize);
        }
    }

    private List<Long> getFeedInRange(String feedCacheKey, long startPostId, long endPostId) {
        var result = redisTemplate.opsForZSet().range(feedCacheKey, startPostId, endPostId);

        if (result == null) {
            return emptyList();
        }

        return result.stream()
                .map(obj -> (Long) obj)
                .toList();
    }

    private void addPostIdToFollowerFeed(Long postId, Long followerId){
        var feedCacheKey = generateFeedCacheKey(followerId);

        redisTemplate.opsForZSet().add(feedCacheKey, postId, currentTimeMillis());

        var setSize = redisTemplate.opsForZSet().zCard(feedCacheKey);
        if (setSize != null && setSize > maxFeedSize) {
            redisTemplate.opsForZSet().removeRange(feedCacheKey, 0, setSize - maxFeedSize);
        }
    }

    private String generateFeedCacheKey(Long followerId) {
        return feedCacheKeyPrefix + followerId;
    }
}