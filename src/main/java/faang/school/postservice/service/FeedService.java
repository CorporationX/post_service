package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.NewsFeedPostDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisAuthorRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author Alexander Bulgakov
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisPostRepository redisPostRepository;
    private final RedisAuthorRepository redisAuthorRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    @Value("${spring.data.redis.stores.keys.feed}")
    private String partKeyStore;
    @Value("${spring.data.redis.posts.count:20}")
    private int count;
    @Value("${spring.data.redis.posts.max-heat-posts:500}")
    private int maxHeatPosts;
    @Value("${spring.data.redis.feed-limit}")
    private int feedLimit;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public List<NewsFeedPostDto> getPostsForUser(Long userId, Long postId) {

        Set<Long> postIds = getPostIdsFromRedis(userId, postId);

        List<NewsFeedPostDto> posts = new ArrayList<>();
        for (Long id : postIds) {
            NewsFeedPostDto newsFeedPostDto = getPostDto(id);
            posts.add(newsFeedPostDto);
        }

        if (posts.size() < count) {
            List<Post> additionalPosts = getPostsFromDatabase(userId, count - posts.size());
            additionalPosts.forEach(post -> posts.add(convertToDto(post)));
        }

        return posts;
    }

    public void heatCache() {
        List<Long> userIds = getAllUsers();

        for (Long userId : userIds) {
            Future<?> future = executor.submit(() -> {
                try {
                    generateFeedForUser(userId, maxHeatPosts);
                } catch (Exception e) {
                    log.error("Error generating feed for user: {}", userId, e);
                }
            });
        }
    }

    private Set<Long> getPostIdsFromRedis(Long userId, Long postId) {
        String feedKey = "feed:" + userId;
        ZSetOperations<String, Object> feed = redisTemplate.opsForZSet();
        Set<Object> postIds;

        if (postId == null) {
            postIds = getFirst20Posts(feed, feedKey, count);
        } else {
            Double lastSeenScore = feed.score(feedKey, postId);
            if (lastSeenScore == null) {
                postIds = getFirst20Posts(feed, feedKey, count);
            } else {
                postIds = feed.reverseRangeByScore(feedKey, -Double.MAX_VALUE, lastSeenScore - 1, 0, count);
            }
        }

        return postIds != null ? postIds.stream()
                .map(Object::toString)
                .map(Long::valueOf)
                .collect(Collectors.toSet())
                : Collections.emptySet();
    }

    private static Set<Object> getFirst20Posts(ZSetOperations<String, Object> feed, String feedKey, int count) {
        return feed.reverseRange(feedKey, 0, count - 1);
    }

    private List<Post> getPostsFromDatabase(Long userId, int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findTopNByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    private void generateFeedForUser(Long userId, int maxHeatPosts) {
        List<Post> postsFromDatabase = getPostsFromDatabase(userId, maxHeatPosts);
        String key = partKeyStore + userId;
        ZSetOperations<String, Object> feed = redisTemplate.opsForZSet();

        Set<TypedTuple<Object>> tuples = postsFromDatabase.stream()
            .map(post -> {
                long timestamp = post.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
                return (TypedTuple<Object>) new DefaultTypedTuple<Object>(post.getId(), (double) -timestamp);
        })
        .collect(Collectors.toSet());

        feed.add(key, tuples);

        ensureFeedLimit(feed, key, feedLimit);
    }

    private void ensureFeedLimit(ZSetOperations<String, Object> feed, String key, int limit) {
        Long size = feed.size(key);
        if (size != null && size > limit) {
            feed.removeRange(key, 0, size - limit - 1);
        }
    }

    private List<Long> getAllUsers() {
        return userServiceClient.getAllUserIds();
    }

    private NewsFeedPostDto getPostDto(Long postId) {
        RedisPost redisPost = redisPostRepository.findById(postId).orElse(null);
        NewsFeedPostDto newsFeedPostDto = new NewsFeedPostDto();

        if (redisPost != null) {
            newsFeedPostDto = NewsFeedPostDto.builder()
                    .id(redisPost.getId())
                    .authorId(redisPost.getAuthorId())
                    .createdAt(redisPost.getCreatedAt())
                    .updatedAt(redisPost.getUpdatedAt())
                    .content(redisPost.getContent())
                    .projectId(redisPost.getProjectId())
                    .comments(redisPost.getComments())
                    .likes(redisPost.getLikes())
                    .build();
        } else {
            Post postInDatabase = getPostFromDatabase(postId);
            if (postInDatabase != null) {
                newsFeedPostDto = convertToDto(postInDatabase);
            }
        }

        return newsFeedPostDto;
    }

    private Post getPostFromDatabase(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new NotFoundException("Post not found with id: " + postId));
    }

    private NewsFeedPostDto convertToDto(Post post) {
        return NewsFeedPostDto.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .content(post.getContent())
                .projectId(post.getProjectId())
                .comments(post.getComments())
                .likes(post.getLikes())
                .build();
    }
}
