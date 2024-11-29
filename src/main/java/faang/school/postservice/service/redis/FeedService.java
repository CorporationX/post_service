package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostNewsFeedDto;
import faang.school.postservice.mapper.redis.CachedPostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CachedPost;
import faang.school.postservice.publisher.kafka.events.FeedHeatEvent;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    @Value("${spring.data.redis.feed-cache.max-size:500}")
    private int maxFeedSize;

    @Value("${spring.data.redis.feed-cache.page-size:20}")
    private int feedPageSize;

    @Value("${spring.data.redis.feed-cache.key-prefix:feed:}")
    private String feedCacheKeyPrefix;

    private final RedisTemplate<String, Object> redisTemplate;
    private final CachedPostService cachedPostService;
    private final CachedPostMapper cachedPostMapper;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CachedAuthorService cachedAuthorService;

    public void addPostToFollowersFeed(Long postId, List<Long> followerIds, LocalDateTime publishedAt) {
        followerIds.forEach(followerId -> addPostToFeed(postId, followerId, publishedAt));
    }

    public List<PostNewsFeedDto> getFeedForUser(Long userId, Long postId) {
        String feedKey = generateFeedKey(userId);

        long start;
        long end;

        if (postId == null) {
            start = 0;
            end = feedPageSize - 1;
        } else {
            Long rank = redisTemplate.opsForZSet().rank(feedKey, postId);
            if (rank == null) {
                start = 0;
                end = feedPageSize - 1;
            } else {
                start = rank + 1;
                end = start + feedPageSize - 1;
            }
        }

        Set<Object> postIds = redisTemplate.opsForZSet().reverseRange(feedKey, start, end);

        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> postIdList = postIds.stream()
                .map(obj -> (Long) obj)
                .collect(Collectors.toList());

        List<CachedPost> posts = cachedPostService.getCachedPostByIds(postIdList);
        return cachedPostMapper.toPostsNewsFeedDto(posts);
    }

    private void addPostToFeed(Long postId, Long userId, LocalDateTime publishedAt) {
        String feedKey = generateFeedKey(userId);
        double score = publishedAt.toInstant(ZoneOffset.UTC).toEpochMilli();

        redisTemplate.opsForZSet().add(feedKey, postId, score);
        redisTemplate.opsForZSet().removeRange(feedKey, 0, -maxFeedSize - 1);

        log.info("Added post ID {} to feed of user {}", postId, userId);
    }


    private String generateFeedKey(Long userId) {
        return feedCacheKeyPrefix + userId;
    }

    public void addPostsToUserFeed(Long userId, List<Post> posts) {
        String feedKey = generateFeedKey(userId);

        Set<ZSetOperations.TypedTuple<Object>> postTuples = posts.stream()
                .map(post -> new DefaultTypedTuple<>((Object) post.getId(),
                        (double) post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli()))
                .collect(Collectors.toSet());

        redisTemplate.opsForZSet().add(feedKey, postTuples);

        Long setSize = redisTemplate.opsForZSet().zCard(feedKey);
        if (setSize != null && setSize > maxFeedSize) {
            redisTemplate.opsForZSet().removeRange(feedKey, 0, setSize - maxFeedSize - 1);
        }

        log.info("Added {} posts to feed of user {}", posts.size(), userId);
    }

    public void processCacheHeatEvent(FeedHeatEvent event) {
        for (Long userId : event.getUserIds()) {
            processUserFeedHeat(userId);
        }
    }

    private void processUserFeedHeat(Long userId) {
        List<Long> authorIds = userServiceClient.getFollowees(userId);

        if (authorIds.isEmpty()) {
            return;
        }
        List<Post> posts = postRepository.findLatestPostsByAuthors(authorIds, PageRequest.of(0, maxFeedSize));

        for (Post post : posts) {
            cachedPostService.savePostCache(post);
            cachedAuthorService.saveAuthorCache(post.getAuthorId());
        }
        addPostsToUserFeed(userId, posts);
    }
}