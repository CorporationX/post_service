package faang.school.postservice.service.feed.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedPostRedisServiceImpl implements FeedPostRedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final JsonUtils jsonUtils;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Value("${app.feed.ttl-hours}")
    private int cacheTtlHours;

    @Value("${app.feed.post-cache-key}")
    private String postDetailsKey;

    @Value("${app.feed.post-feed}")
    private String userFeedPostsKey;

    @Value("${app.feed.post-batch-size}")
    private int postBatchSize;

    @Value("${app.feed.post-start-from}")
    private String userPostFeedOffsetKey;

    @Override
    public boolean isPostAvailableInCache(Long userId, int offset) {
        String postFeedKey = getUserFeedPostsKey(userId, offset);
        List<String> postIds = redisTemplate.opsForList().range(postFeedKey, 0, -1);
        if (postIds == null || postIds.size() < postBatchSize) {
            return false;
        }
        for (String postId : postIds) {
            if (!redisTemplate.hasKey(getPostKey(Long.valueOf(postId)))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<FeedPostDto> loadPostsFromCache(Long userId, int offset) {
        String postFeedKey = getUserFeedPostsKey(userId, offset);
        List<String> postIds = redisTemplate.opsForList().range(postFeedKey, 0, -1);

        List<FeedPostDto> result = new ArrayList<>();
        for (String postId : postIds) {
            String postKey = getPostKey(Long.valueOf(postId));
            Map<Object, Object> fields = redisTemplate.opsForHash().entries(postKey);
            FeedPostDto feedPostDto = jsonUtils.convertMapToClass(fields, FeedPostDto.class);
            result.add(feedPostDto);
        }
        return result;
    }

    @Override
    public void cachePostIdForUser(Long userId, Long postId, int offset) {
        String postFeedKey = getUserFeedPostsKey(userId, offset);
        redisTemplate.opsForList().rightPush(postFeedKey, String.valueOf(postId));
        redisTemplate.expire(postFeedKey, Duration.ofHours(cacheTtlHours));
        log.info("Post with ID {} has been cached for user {}", postId, userId);
    }

    @Override
    public void cachePostDetails(FeedPostDto feedPostDto) {
        if (feedPostDto.getAuthorId() != null) {
            feedPostDto.setAuthorName(userServiceClient.getUser(feedPostDto.getAuthorId()).username());
        } else {
            feedPostDto.setProjectName(Objects.requireNonNull(
                    projectServiceClient.getProject(feedPostDto.getProjectId()).getBody()).name());
        }
        String postKey = getPostKey(feedPostDto.getId());
        Map<String, String> json = jsonUtils.toMap(feedPostDto);
        redisTemplate.opsForHash().putAll(postKey, json);
        redisTemplate.expire(postKey, Duration.ofHours(cacheTtlHours));
        log.info("Post: {} saved to cache", json);
    }

    @Override
    public void removePostFromCache(Long postId) {
        String postKey = getPostKey(postId);
        if (redisTemplate.hasKey(postKey)) {
            redisTemplate.delete(postKey);
            log.info("Post with ID {} deleted from cache", postId);
        } else {
            log.warn("Post with ID {} not found in cache", postId);
        }
    }

    @Override
    public void incrementPostLikes(Long postId) {
        String postKey = getPostKey(postId);
        if (redisTemplate.hasKey(postKey)) {
            redisTemplate.opsForHash().increment(postKey, "likes", 1);
            log.info("Add like in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't add like. Post with ID {} not found in cache", postId);
        }
    }

    @Override
    public void decrementPostLikes(Long postId) {
        String key = getPostKey(postId);
        if (redisTemplate.hasKey(key)) {
            redisTemplate.opsForHash().increment(key, "likes", -1);
            log.info("Remove likes in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't remove like. Post with ID {} not found in cache", postId);
        }
    }

    @Override
    public void incrementPostComments(Long postId) {
        String commentKey = getPostKey(postId);
        if (redisTemplate.hasKey(commentKey)) {
            redisTemplate.opsForHash().increment(commentKey, "comments", 1);
            log.info("Add comment in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't add comment. Post with ID {} not found in cache", postId);
        }
    }

    @Override
    public void decrementPostComments(Long postId) {
        String commentKey = getPostKey(postId);
        if (redisTemplate.hasKey(commentKey)) {
            redisTemplate.opsForHash().increment(commentKey, "comments", -1);
            log.info("Removed comment in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't remove comment. Post with ID {} not found in cache", postId);
        }
    }

    @Override
    public void incrementPostViews(Long postId) {
        String key = getPostKey(postId);
        if (redisTemplate.hasKey(key)) {
            redisTemplate.opsForHash().increment(key, "views", 1);
            log.info("Incremented views in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't increment views. Post with ID {} not found in cache", postId);
        }
    }

    @Override
    public void preloadUserPosts(Long userId) {
        updateUserPostOffset(userId, 0);
        int offset = 0;
        List<Long> followees = userServiceClient.getFollowees(userId);

        Pageable pageable = PageRequest.of(0, postBatchSize);
        List<Post> posts = postRepository.findPublishedPostsByAuthorIds(pageable, followees).getContent();

        posts.forEach(post -> {
            cachePostIdForUser(userId, post.getId(), offset);
            cachePostDetails(postMapper.toFeedPostDto(post));
        });
        log.info("Finished heat posts for user with ID: {}", userId);
    }


    @Override
    public void updateUserPostOffset(Long userId, int value) {
        redisTemplate.opsForValue().set(getPostFeedStartFromKey(userId),
                String.valueOf(value), cacheTtlHours);
    }

    private String getPostKey(Long postId) {
        return postDetailsKey.formatted(postId);
    }

    private String getUserFeedPostsKey(Long userId, int offset) {
        return userFeedPostsKey.formatted(userId, offset);
    }

    private String getPostFeedStartFromKey(Long userId) {
        return userPostFeedOffsetKey.formatted(userId);
    }
}
