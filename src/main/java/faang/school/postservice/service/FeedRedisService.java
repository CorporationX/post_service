package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.utils.JsonUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedRedisService {
    private static final String ZSET_TRIM_SCRIPT =
            """
                redis.call('ZADD', KEYS[1], ARGV[1], ARGV[2])
                local size = redis.call('ZCARD', KEYS[1])
                if size > tonumber(ARGV[3]) then
                     redis.call('ZREMRANGEBYRANK', KEYS[1], 0, size - tonumber(ARGV[3]) - 1)
                end
                return size
            """;

    private final RedisTemplate<String, String> redisTemplate;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final ZSetOperations<String, String> zSetOperations;
    private final JsonUtils jsonUtils;
    private final DefaultRedisScript<Long> script = new DefaultRedisScript<>();

    @PostConstruct
    public void setUp() {
        script.setScriptText(ZSET_TRIM_SCRIPT);
        script.setResultType(Long.class);
    }

    @Value("${app.feed.ttl-hours}")
    private int ttlHours;

    @Value("${app.feed.post-cache-key}")
    private String postCacheKey;

    @Value("${app.feed.post-comments-cache-key}")
    private String postCommentsCacheKey;

    @Value("${app.feed.comment-cache-key}")
    private String commentCacheKey;

    @Value("${app.feed.max-cached-comments-for-post}")
    private String maxCachedCommentsForPost;

    public void createPost(FeedPostDto feedPostDto) {
        if (feedPostDto.getAuthorId() != null) {
            feedPostDto.setAuthorName(userServiceClient.getUser(feedPostDto.getAuthorId()).username());
        } else {
            feedPostDto.setProjectName(Objects.requireNonNull(
                    projectServiceClient.getProject(feedPostDto.getProjectId()).getBody()).name());
        }
        String postKey = getPostKey(feedPostDto.getId());
        Map<String, Object> json = jsonUtils.toMap(feedPostDto);
        redisTemplate.opsForHash().putAll(postKey, json);
        redisTemplate.expire(postKey, Duration.ofHours(ttlHours));
        log.info("Post: {} saved to cache", json);
    }

    public void removePost(Long postId) {
        String postKey = getPostKey(postId);
        if (redisTemplate.hasKey(postKey)) {
            redisTemplate.delete(postKey);
            log.info("Post with ID {} deleted from cache", postId);
        } else {
            log.warn("Post with ID {} not found in cache", postId);
        }
    }

    public void incrementPostLikes(Long postId) {
        String postKey = getPostKey(postId);
        if (redisTemplate.hasKey(postKey)) {
            redisTemplate.opsForHash().increment(postKey, "likes", 1);
            log.info("Add like in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't add like. Post with ID {} not found in cache", postId);
        }
    }

    public void decrementPostLikes(Long postId) {
        String key = getPostKey(postId);
        if (redisTemplate.hasKey(key)) {
            redisTemplate.opsForHash().increment(key, "likes", -1);
            log.info("Remove likes in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't remove like. Post with ID {} not found in cache", postId);
        }
    }

    public void incrementComments(Long postId) {
        String commentKey = getCommentKey(postId);
        if (redisTemplate.hasKey(commentKey)) {
            redisTemplate.opsForHash().increment(commentKey, "comments", 1);
            log.info("Add comment in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't add comment. Post with ID {} not found in cache", postId);
        }
    }

    public void decrementComments(Long postId) {
        String commentKey = getCommentKey(postId);
        if (redisTemplate.hasKey(commentKey)) {
            redisTemplate.opsForHash().increment(commentKey, "comments", -1);
            log.info("Removed comment in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't remove comment. Post with ID {} not found in cache", postId);
        }
    }

    public void addCommentToPost(Long postId, FeedCommentDto feedCommentDto) {
        String commentKey = getCommentKey(feedCommentDto.getId());
        String postCommentsKey = getPostCommentsKey(postId);
        String json = jsonUtils.serialize(feedCommentDto);
        double score = Instant.now().toEpochMilli();

        redisTemplate.opsForValue().set(commentKey, json, Duration.ofHours(ttlHours));

        redisTemplate.execute(
                script,
                List.of(postCommentsKey),
                String.valueOf(score),
                String.valueOf(feedCommentDto.getId()),
                maxCachedCommentsForPost
        );
        redisTemplate.expire(postCommentsKey, Duration.ofHours(ttlHours));
        log.info("Comment: {} saved to cache", json);
    }

    public void removeCommentFromPost(Long postId, Long commentId) {
        String commentKey = getPostCommentsKey(postId);
        String postCommentsKey = getPostCommentsKey(postId);
        if (redisTemplate.hasKey(commentKey)) {
            redisTemplate.delete(commentKey);
            zSetOperations.remove(postCommentsKey, String.valueOf(commentId));
            log.info("Comment with ID {} has been deleted from cache", commentId);
        } else {
            log.warn("Can't delete comment. Comment with ID {} not found in cache", commentId);
        }
    }

    public void updateComment(Long postId, FeedCommentDto feedCommentDto) {
        String commentKey = getPostCommentsKey(postId);
        String postCommentsKey = getPostCommentsKey(postId);
        String json = jsonUtils.serialize(feedCommentDto);
        String commentId = String.valueOf(feedCommentDto.getId());

        boolean commentExists = redisTemplate.hasKey(commentKey);
        Double currentScore = zSetOperations.score(postCommentsKey, commentId);
        if (!commentExists || currentScore == null) {
            log.warn("Can't update comment. Either key {} or comment with ID {} not found in cache {}",
                    commentKey, commentId, postCommentsKey);
            return;
        }
        redisTemplate.delete(commentKey);
        zSetOperations.remove(postCommentsKey, commentId);
        zSetOperations.add(postCommentsKey, String.valueOf(feedCommentDto.getId()), currentScore);
        redisTemplate.expire(postCommentsKey, Duration.ofHours(ttlHours));
        log.info("Comment with id: {} updated in cache", json);
    }

    public void addView(Long postId) {
        String key = getPostKey(postId);
        if (redisTemplate.hasKey(key)) {
            redisTemplate.opsForHash().increment(key, "views", 1);
            log.info("Incremented views in cache for post with ID: {}", postId);
        } else {
            log.warn("Can't increment views. Post with ID {} not found in cache", postId);
        }
    }

    private String getPostKey(Long postId) {
        return postCacheKey + postId;
    }

    private String getPostCommentsKey(Long postId) {
        return postCommentsCacheKey.formatted(postId);
    }

    private String getCommentKey(Long commentId) {
        return commentCacheKey.formatted(commentId);
    }
}
