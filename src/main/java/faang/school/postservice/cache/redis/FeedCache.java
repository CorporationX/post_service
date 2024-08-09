package faang.school.postservice.cache.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.FeedHeaterEvent;
import faang.school.postservice.dto.post.CachedPostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.producer.kafka.FeedHeaterProducer;
import faang.school.postservice.service.post.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Component
@Slf4j
public class FeedCache {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, Object> operations;
    private final UserCache userCache;
    private final UserServiceClient userServiceClient;
    private final PostCache postCache;
    private final PostService postService;
    private final FeedHeaterProducer feedHeaterProducer;

    @Value("${spring.data.redis.key-spaces.feed.prefix}")
    private String keyPrefix;

    @Value("${spring.data.redis.max-feed-size}")
    private int maxSize;

    @Value("${spring.data.redis.key-spaces.feed.ttl}")
    private long feedTTL;

    @Value("${spring.data.redis.key-spaces.feed.batch-size}")
    private int feedBatchSize;

    @Value("${spring.data.redis.key-spaces.feed.feed-heater.batch-size}")
    private int feedHeaterBatchSize;

    public FeedCache(RedisTemplate<String, Object> redisTemplate, UserCache userCache,
                     UserServiceClient userServiceClient, PostCache postCache,
                     PostService postService,
                     FeedHeaterProducer feedHeaterProducer) {
        this.redisTemplate = redisTemplate;
        this.operations = redisTemplate.opsForZSet();
        this.userCache = userCache;
        this.userServiceClient = userServiceClient;
        this.postCache = postCache;
        this.postService = postService;
        this.feedHeaterProducer = feedHeaterProducer;
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

    public void heat() {
        List<Long> userIds = userServiceClient.getAllUserIds();
        List<List<Long>> separatedUserIds = splitList(userIds);
        separatedUserIds.stream()
                .map(FeedHeaterEvent::new)
                .forEach(feedHeaterProducer::sendEvent);
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

    private List<List<Long>> splitList(List<Long> ids) {
        return IntStream
                .range(0, (ids.size() + feedHeaterBatchSize - 1) / feedHeaterBatchSize)
                .mapToObj(num -> ids.subList(num * feedHeaterBatchSize, Math.min(feedHeaterBatchSize * (num +1), ids.size())))
                .toList();
    }
}