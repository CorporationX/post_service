package faang.school.postservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.FeedPostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedRedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Value("${app.feed.ttl-hours}")
    private int ttlHours;

    @Value("${app.feed.post-cache-key}")
    private String postCachePrefix;

    @Value("${app.feed.post-comments-cache-key}")
    private String postCommentsCachePrefix;

    public void createPost(FeedPostDto feedPostDto) {
        if (feedPostDto.getAuthorId() != null) {
            feedPostDto.setAuthorName(userServiceClient.getUser(feedPostDto.getAuthorId()).username());
        } else {
            feedPostDto.setProjectName(Objects.requireNonNull(
                    projectServiceClient.getProject(feedPostDto.getProjectId()).getBody()).name());
        }
        String key = getPostKey(feedPostDto.getId());
        Map<String, String> postFields = objectMapper.convertValue(feedPostDto, new TypeReference<Map<String, String>>() {
        });
        redisTemplate.opsForHash().putAll(key, postFields);
        redisTemplate.expire(key, Duration.ofHours(ttlHours));
        log.info("Cached post: {}", postFields);
    }

    public void removePost(Long postId) {
        String key = getPostKey(postId);
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
            log.info("Post with ID {} deleted from cache", postId);
        }
    }

    public void addLikeToPost(Long postId) {
        String key = getPostKey(postId);
        if (redisTemplate.hasKey(key)) {
            redisTemplate.opsForHash().increment(key, "likes", 1);
            log.info("Add like in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't add like. Post with ID {} has been deleted from cache", postId);
        }
    }

    public void removeLikeFromPost(Long postId) {
        String key = getPostKey(postId);
        if (redisTemplate.hasKey(key)) {
            redisTemplate.opsForHash().increment(key, "likes", -1);
            log.info("Remove likes in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't remove like. Post with ID {} has been deleted from cache", postId);
        }
    }

    public void addComment(Long postId) {
        String key = getPostKey(postId);
        if (redisTemplate.hasKey(key)) {
            redisTemplate.opsForHash().increment(key, "comments", 1);
            log.info("Add comment in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't add comment. Post with ID {} has been deleted from cache", postId);
        }
    }

    public void removeComment(Long postId) {
        String key = getPostKey(postId);
        if (redisTemplate.hasKey(key)) {
            redisTemplate.opsForHash().increment(key, "comments", -1);
            log.info("Removed comment in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't remove comment. Post with ID {} has been deleted from cache", postId);
        }
    }

    public void addView(Long postId) {
        String key = getPostKey(postId);
        if (redisTemplate.hasKey(key)) {
            redisTemplate.opsForHash().increment(key, "views", 1);
            log.info("Incremented views in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't increment views. Post with ID {} has been deleted from cache", postId);
        }
    }

    private String getPostKey(Long postId) {
        return postCachePrefix + postId;
    }

    private String getCommentKey(Long postId) {
        return postCommentsCachePrefix.formatted(postId);
    }
}
