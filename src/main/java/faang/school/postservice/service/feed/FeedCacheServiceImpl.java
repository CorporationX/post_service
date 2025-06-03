package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.CacheOperationException;
import faang.school.postservice.exception.CacheWarmingException;
import faang.school.postservice.exception.PostDetailException;
import faang.school.postservice.exception.PostRetrievalException;
import faang.school.postservice.exception.ServiceUnavailableException;
import faang.school.postservice.exception.UserRetrievalException;
import faang.school.postservice.mapper.PostCacheMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static faang.school.postservice.contants.ErrorMessage.ERROR_CACHE_FAILED;
import static faang.school.postservice.contants.ErrorMessage.ERROR_CACHE_WARMING;
import static faang.school.postservice.contants.ErrorMessage.ERROR_RETRIEVE_POSTS;
import static faang.school.postservice.contants.ErrorMessage.ERROR_RETRIEVE_POST_DETAILS;
import static faang.school.postservice.contants.ErrorMessage.ERROR_RETRIEVE_USER;
import static faang.school.postservice.contants.ErrorMessage.ERROR_SERVICE_UNAVAILABLE;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedCacheServiceImpl implements FeedCacheService {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostMapper postMapper;
    private final PostCacheMapper postCacheMapper;

    @Value("${app.feed.posts-limit}")
    private int postsLimit;

    @Value("${app.redis.keys.user-feed}")
    private String userFeedKeyPattern;

    @Value("${app.redis.keys.user-data}")
    private String userDataKeyPattern;

    @Value("${app.redis.keys.post-data}")
    private String postDataKeyPattern;

    @Value("${app.redis.ttl.default-seconds}")
    private long defaultTtlSeconds;

    @Override
    public void warmUpCacheForUser(Long userId) {
        try {
            Set<Long> subscriptionIds = userServiceClient.getSubscriptions(userId);
            if (subscriptionIds.isEmpty()) {
                log.info("User {} has no subscriptions. Skipping feed warmup.", userId);
                return;
            }

            List<Post> feedPosts = getLatestPosts(subscriptionIds);
            if (feedPosts.isEmpty()) {
                log.warn("No posts found for user {}", userId);
                return;
            }

            saveUserFeed(userId, feedPosts);

            Set<Long> allAuthors = new HashSet<>();
            allAuthors.addAll(extractAuthorIds(feedPosts));
            allAuthors.addAll(extractCommentAuthorIds(feedPosts));
            saveAuthors(allAuthors);

            Set<Long> postIds = extractPostIds(feedPosts);
            savePostDetails(postIds);

            log.info("Cache warmed for user {}", userId);
        } catch (FeignException e) {
            log.error("Service communication failed for user {}", userId, e);
            throw new ServiceUnavailableException("User service unavailable");
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed for user {}", userId, e);
            throw new CacheOperationException(ERROR_CACHE_FAILED);
        } catch (DataAccessException e) {
            log.error("Database error for user {}", userId, e);
            throw new PersistenceException(ERROR_SERVICE_UNAVAILABLE);
        } catch (PostRetrievalException e) {
            throw new PostRetrievalException(ERROR_RETRIEVE_POSTS);
        } catch (Exception e) {
            log.error("Failed to warm cache for user {}", userId, e);
            throw new CacheWarmingException(ERROR_CACHE_WARMING);
        }
    }

    private List<Post> getLatestPosts(Set<Long> subscriptionIds) {
        try {
            return postRepository.findByAuthorIdsAndPublishedTrue(subscriptionIds,
                    PageRequest.of(0, postsLimit, Sort.by(Sort.Direction.DESC, "publishedAt")));
        } catch (DataAccessException e) {
            log.error("Failed to retrieve latest posts", e);
            throw new PostRetrievalException(ERROR_RETRIEVE_POSTS);
        }
    }

    private void saveUserFeed(Long userId, List<Post> feedPosts) {
        String key = String.format(userFeedKeyPattern, userId);
        RedisSerializer<Object> serializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            connection.del(key.getBytes());

            List<PostResponseDto> dtos = postMapper.toPostResponseDtoList(feedPosts);
            for (PostResponseDto dto : dtos) {
                byte[] data = serializer.serialize(dto);
                connection.rPush(key.getBytes(), data);
            }

            connection.expire(key.getBytes(), defaultTtlSeconds);
            return null;
        });
    }

    private void saveAuthors(Set<Long> authorIds) {
        if (authorIds.isEmpty()) return;

        try {
            List<UserDto> users = userServiceClient.getUsersByIds(new ArrayList<>(authorIds));
            RedisSerializer<Object> serializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                users.forEach(user -> {
                    String key = String.format(userDataKeyPattern, user.id());
                    byte[] serializedUser = serializer.serialize(user);
                    connection.set(key.getBytes(), serializedUser);
                    connection.expire(key.getBytes(), defaultTtlSeconds);
                });
                return null;
            });
        } catch (FeignException e) {
            log.error("Failed to retrieve users by IDs", e);
            throw new UserRetrievalException(ERROR_RETRIEVE_USER);
        }
    }

    private void savePostDetails(Set<Long> postIds) {
        if (postIds.isEmpty()) return;

        try {
            List<Post> posts = postRepository.findAllByIdWithComments(postIds);
            if (posts.isEmpty()) return;

            Map<Long, PostCacheDto> postDetails = posts.stream()
                    .collect(Collectors.toMap(
                            Post::getId,
                            postCacheMapper::postToCacheDto
                    ));

            RedisSerializer<Object> serializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                postDetails.forEach((id, details) -> {
                    String key = String.format(postDataKeyPattern, id);
                    byte[] serializedDetails = serializer.serialize(details);
                    connection.set(key.getBytes(), serializedDetails);
                    connection.expire(key.getBytes(), defaultTtlSeconds);
                });
                return null;
            });
        } catch (DataAccessException e) {
            log.error("Failed to retrieve post details", e);
            throw new PostDetailException(ERROR_RETRIEVE_POST_DETAILS);
        }
    }

    private Set<Long> extractAuthorIds(List<Post> posts) {
        return posts.stream().map(Post::getAuthorId).collect(Collectors.toSet());
    }

    private Set<Long> extractPostIds(List<Post> posts) {
        return posts.stream().map(Post::getId).collect(Collectors.toSet());
    }

    private Set<Long> extractCommentAuthorIds(List<Post> posts) {
        return posts.stream().flatMap(post -> post.getComments().stream()).map(Comment::getAuthorId)
                .collect(Collectors.toSet());
    }
}
