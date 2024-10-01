package faang.school.postservice.repository.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CachedPostDto;
import faang.school.postservice.dto.publishable.FeedHeaterEvent;
import faang.school.postservice.dto.user.CachedUserDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.producer.KafkaFeedHeaterProducer;
import faang.school.postservice.service.post.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


@Component
@Slf4j
public class RedisFeedRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, Object> zSet;
    private final PostService postService;
    private final RedisPostRepository postCache;
    private final RedisUserRepository userCache;
    private final UserServiceClient userServiceClient;
    private final KafkaFeedHeaterProducer feedHeaterProducer;


    @Value("${spring.data.redis.feed.prefix}")
    private String keyPrefix;

    @Value("${spring.data.redis.feed.max-feed-size}")
    private int maxSize;

    @Value("${spring.data.redis.feed.TTL}")
    private long feedTTL;

    @Value("${spring.data.redis.feed.batch-size}")
    private int feedBatchSize;


    public RedisFeedRepository(RedisTemplate<String, Object> redisTemplate,
                               PostService postService,
                               RedisPostRepository postCache,
                               RedisUserRepository userCache,
                               UserServiceClient userServiceClient,
                               KafkaFeedHeaterProducer feedHeaterProducer) {
        this.redisTemplate = redisTemplate;
        this.zSet = redisTemplate.opsForZSet();
        this.postService = postService;
        this.postCache = postCache;
        this.userCache = userCache;
        this.userServiceClient = userServiceClient;
        this.feedHeaterProducer = feedHeaterProducer;
    }

    public void save(Long userId, Long postId) {
        try {
            String key = keyPrefix + userId;
            zSet.add(key, String.valueOf(postId), System.currentTimeMillis());
            redisTemplate.expire(key, feedTTL, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("Failed to add post to feed for user {}: {}", userId, e.getMessage(), e);
        }
    }

    public List<CachedPostDto> getFeed(Long userId, Long startPostId) {
        startPostId = startPostId == null ? 1L : startPostId;
        Set<Object> setOfPostIds = zSet.reverseRange(keyPrefix + userId, 0, maxSize - 1);
        if (setOfPostIds == null || setOfPostIds.isEmpty()) {
            log.info("get posts from database");
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

    private UserDto getUserDto(Long userId) {
        CachedUserDto cachedUserDto = userCache.get(userId).orElse(null);
        UserDto userDto;
        if (cachedUserDto == null) {
            userDto = userServiceClient.getUser(userId);
            if (userDto == null) {
                log.error("user with id {} not found", userId);
                throw new EntityNotFoundException(String.format("user with id = %s not found", userId));
            }
            cachedUserDto = CachedUserDto.builder()
                    .id(userDto.getId())
                    .email(userDto.getEmail())
                    .username(userDto.getUsername())
                    .subscriberIds(userDto.getSubscriberIds())
                    .build();
            userCache.save(cachedUserDto.getId(), cachedUserDto);
        } else {
            userDto = UserDto.builder()
                    .id(cachedUserDto.getId())
                    .username(cachedUserDto.getUsername())
                    .email(cachedUserDto.getEmail())
                    .subscriberIds(cachedUserDto.getSubscriberIds())
                    .build();
        }

        return userDto;
    }


    private List<CachedPostDto> mapPostIdsToPostDtos(List<Long> postIds) {
        return postIds.stream()
                .map(postId ->
                        postCache.get(postId)
                                .orElseGet(() -> postService.getPostFromCache(postId))
                )
                .limit(feedBatchSize)
                .toList();
    }

    private List<Long> convertSetOfObjectsToLongList(Set<Object> postIds) {
        return postIds
                .stream()
                .map(obj -> Long.parseLong(obj.toString()))
                .toList();
    }

    public void heat() {
        List<Long> userIds = userServiceClient.getAllUserIds();
        List<List<Long>> separatedIds = splitList(userIds);
        separatedIds.stream()
                .map(FeedHeaterEvent::new)
                .forEach(feedHeaterProducer::sendEvent);
    }

    private List<List<Long>> splitList(List<Long> ids) {
        return IntStream
                .range(0, (ids.size() + feedBatchSize - 1) / feedBatchSize)
                .mapToObj(num -> ids.subList(num * feedBatchSize, Math.min(feedBatchSize * (num + 1), ids.size())))
                .toList();
    }

}
