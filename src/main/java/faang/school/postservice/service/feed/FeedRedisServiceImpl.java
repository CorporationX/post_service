package faang.school.postservice.service.feed;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
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
public class FeedRedisServiceImpl implements FeedRedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final JsonUtils jsonUtils;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    @Value("${app.feed.ttl-hours}")
    private int cacheTtlHours;

    @Value("${app.feed.post-cache-key}")
    private String postDetailsKey;

    @Value("${app.feed.comment-cache-key}")
    private String commentDetailsKey;

    @Value("${app.feed.post-feed}")
    private String userFeedPostsKey;

    @Value("${app.feed.comment-feed}")
    private String postFeedCommentsKey;

    @Value("${app.feed.post-batch-size}")
    private int postBatchSize;

    @Value("${app.feed.comment-batch-size}")
    private int commentBatchSize;

    @Value("${app.feed.post-start-from}")
    private String userPostFeedOffsetKey;

    @Value("${app.feed.comment-start-from}")
    private String postCommentFeedOffsetKey;

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
    public boolean isCommentAvailableInCache(Long postId, int offset) {
        String commentFeedKey = getPostFeedCommentsKey(postId, offset);
        List<String> commentIds = redisTemplate.opsForList().range(commentFeedKey, 0, -1);
        if (commentIds == null || commentIds.size() < commentBatchSize) {
            return false;
        }
        for (String commentId : commentIds) {
            if (!redisTemplate.hasKey(getCommentKey(Long.valueOf(commentId)))) {
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
    public List<FeedCommentDto> loadCommentsFromCache(Long postId, int offset) {
        String commentFeedKey = getPostFeedCommentsKey(postId, offset);
        List<String> commentIds = redisTemplate.opsForList().range(commentFeedKey, 0, -1);

        List<FeedCommentDto> result = new ArrayList<>();
        for (String commentId : commentIds) {
            String commentKey = getCommentKey(Long.valueOf(commentId));
            Map<Object, Object> fields = redisTemplate.opsForHash().entries(commentKey);
            FeedCommentDto commentDto = jsonUtils.convertMapToClass(fields, FeedCommentDto.class);
            result.add(commentDto);
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
    public void cacheCommentIdForPost(Long postId, Long commentId, int offset) {
        String commentsKey = getPostFeedCommentsKey(postId, offset);
        redisTemplate.opsForList().rightPush(commentsKey, String.valueOf(commentId));
        redisTemplate.expire(commentsKey, Duration.ofHours(cacheTtlHours));
        log.info("Comment with ID {} has been cached for post {}", commentId, postId);
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
    public void incrementCommentLikes(Long commentId) {
        String commentKey = getCommentKey(commentId);
        if (redisTemplate.hasKey(commentKey)) {
            redisTemplate.opsForHash().increment(commentKey, "likes", 1);
            log.info("Add like in cache for comment with ID: {}", commentId);
        } else {
            log.warn("Can't add like. Comment with ID {} not found in cache", commentId);
        }
    }

    @Override
    public void decrementCommentLikes(Long commentId) {
        String commentKey = getCommentKey(commentId);
        if (redisTemplate.hasKey(commentKey)) {
            redisTemplate.opsForHash().increment(commentKey, "likes", -1);
            log.info("Remove like in cache for comment with ID: {}", commentId);
        } else {
            log.warn("Can't remove like. Comment with ID {} not found in cache", commentId);
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
    public void cacheCommentDetails(FeedCommentDto feedCommentDto) {
        String commentKey = getCommentKey(feedCommentDto.getId());
        Map<String, String> json = jsonUtils.toMap(feedCommentDto);

        redisTemplate.opsForHash().putAll(commentKey, json);
        redisTemplate.expire(commentKey, Duration.ofHours(cacheTtlHours));
        log.info("Comment: {} saved to cache", json);
    }

    @Override
    public void removeCommentFromCache(Long commentId) {
        String commentKey = getCommentKey(commentId);
        if (redisTemplate.hasKey(commentKey)) {
            redisTemplate.delete(commentKey);
            log.info("Comment with ID {} deleted from cache", commentId);
        } else {
            log.warn("Comment with ID {} not found in cache", commentId);
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
    public void preloadPostComments(Long postId) {
        updatePostCommentsOffset(postId, 0);
        int offset = 0;
        Pageable pageable = PageRequest.of(0, commentBatchSize);
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(pageable, postId).getContent();

        comments.forEach(comment -> {
            cacheCommentIdForPost(postId, comment.getId(), offset);
            cacheCommentDetails(commentMapper.toFeedCommentDto(comment));
        });
        log.info("Finished heat comments for post with ID: {}", postId);
    }

    @Override
    public void updateUserPostOffset(Long userId, int value) {
        redisTemplate.opsForValue().set(getPostFeedStartFromKey(userId),
                String.valueOf(value), cacheTtlHours);
    }

    @Override
    public void updatePostCommentsOffset(Long postId, int value) {
        redisTemplate.opsForValue().set(getCommentFeedStartFromKey(postId),
                String.valueOf(value), cacheTtlHours);
    }

    private String getPostKey(Long postId) {
        return postDetailsKey.formatted(postId);
    }

    private String getCommentKey(Long commentId) {
        return commentDetailsKey.formatted(commentId);
    }

    private String getUserFeedPostsKey(Long userId, int offset) {
        return userFeedPostsKey.formatted(userId, offset);
    }

    private String getPostFeedCommentsKey(Long postId, int offset) {
        return postFeedCommentsKey.formatted(postId, offset);
    }

    private String getPostFeedStartFromKey(Long userId) {
        return userPostFeedOffsetKey.formatted(userId);
    }

    private String getCommentFeedStartFromKey(Long postId) {
        return postCommentFeedOffsetKey.formatted(postId);
    }
}
