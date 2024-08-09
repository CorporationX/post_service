package faang.school.postservice.cache.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CachedPostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.post.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class FeedCache {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, Object> operations;
    private final UserCache userCache;
    private final UserServiceClient userServiceClient;
    private final PostCache postCache;
    private final PostService postService;

    @Value("${spring.data.redis.key-spaces.feed.prefix}")
    private String keyPrefix;

    @Value("${spring.data.redis.max-feed-size}")
    private int maxSize;

    @Value("${spring.data.redis.key-spaces.feed.ttl}")
    private long feedTTL;

    @Value("${spring.data.redis.key-spaces.feed.batch-size}")
    private long feedBatchSize;

    public FeedCache(RedisTemplate<String, Object> redisTemplate, UserCache userCache,
                     UserServiceClient userServiceClient, PostCache postCache,
                     PostService postService) {
        this.redisTemplate = redisTemplate;
        this.operations = redisTemplate.opsForZSet();
        this.userCache = userCache;
        this.userServiceClient = userServiceClient;
        this.postCache = postCache;
        this.postService = postService;
    }

    public void save(Long userId, Long postId) {
        String key = keyPrefix + userId;
        operations.add(key, postId, System.currentTimeMillis());
        redisTemplate.expire(key, feedTTL, TimeUnit.DAYS);
    }

    public List<CachedPostDto> getFeed(Long userId, Long startPostId) {
        startPostId = startPostId == null ? 1L : startPostId;
        Set<Object> setOfPostIds = operations.reverseRange(keyPrefix + userId, 0, maxSize - 1);
        if (setOfPostIds == null || setOfPostIds.isEmpty()) {
            log.info("get posts from db");
            UserDto userDto = getUserDto(userId);
            List<CachedPostDto> postDtos = postService.getPostsByAuthorIds(userDto.getSubscriberIds(), startPostId, feedBatchSize);
            postCache.saveAll(postDtos);
            return postDtos;
        }
        List<Long> postIds = convertSetOfObjectsToLongList(setOfPostIds);
        List<CachedPostDto> result = mapPostIdsToPostDtos(postIds);
        if (result.size() < feedBatchSize) {
            log.info("not enough posts in cache! get posts from db");
            UserDto userDto = getUserDto(userId);
            List<CachedPostDto> addedPosts = postService.getPostsByAuthorIds(userDto.getSubscriberIds(), startPostId,
                    feedBatchSize - result.size());
            result.addAll(addedPosts);
        }
        return result;
    }

    private List<CachedPostDto> mapPostIdsToPostDtos(List<Long> postIds) {
        return postIds.stream()
                .map(postId ->
                        postCache.findById(postId)
                                .orElseGet(() -> postService.getCachedPostById(postId))
                )
                .limit(feedBatchSize)
                .toList();
    }

    private List<Long> convertSetOfObjectsToLongList(Set<Object> postIds) {
        return postIds
                .stream()
                .map(obj -> (Long) obj)
                .toList();
    }

    private UserDto getUserDto(Long userId) {
        UserDto userDto = userCache.findById(userId);
        if (userDto == null) {
            userDto = userServiceClient.getUser(userId);
            if (userDto == null) {
                log.error("user with id {} not found", userId);
                throw new EntityNotFoundException(String.format("user with id = %s not found", userId));
            }
            userCache.save(userDto);
        }
        return userDto;
    }
}