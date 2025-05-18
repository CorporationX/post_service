package faang.school.postservice.service.feed.comment;

import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCommentRedisServiceImpl implements FeedCommentRedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JsonUtils jsonUtils;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Value("${app.feed.ttl-hours}")
    private int cacheTtlHours;

    @Value("${app.feed.comment-cache-key}")
    private String commentDetailsKey;

    @Value("${app.feed.comment-feed}")
    private String postFeedCommentsKey;

    @Value("${app.feed.comment-batch-size}")
    private int commentBatchSize;

    @Value("${app.feed.post-comment-start-from}")
    private String postCommentFeedOffsetKey;

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
    public void cacheCommentIdForPost(Long postId, Long commentId, int offset) {
        String commentsKey = getPostFeedCommentsKey(postId, offset);
        redisTemplate.opsForList().rightPush(commentsKey, String.valueOf(commentId));
        redisTemplate.expire(commentsKey, Duration.ofHours(cacheTtlHours));
        log.info("Comment with ID {} has been cached for post {}", commentId, postId);
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
    public void cacheCommentDetails(FeedCommentDto feedCommentDto) {
        String commentKey = getCommentKey(feedCommentDto.getId());
        Map<String, String> json = jsonUtils.toMap(feedCommentDto);

        redisTemplate.opsForHash().putAll(commentKey, json);
        redisTemplate.expire(commentKey, Duration.ofHours(cacheTtlHours));
        log.info("Comment: {} saved to cache", json);
    }

    @Override
    public void updatePostCommentsOffset(Long postId, int value) {
        redisTemplate.opsForValue().set(getCommentFeedStartFromKey(postId),
                String.valueOf(value), cacheTtlHours);
    }

    private String getCommentKey(Long commentId) {
        return commentDetailsKey.formatted(commentId);
    }

    private String getPostFeedCommentsKey(Long postId, int offset) {
        return postFeedCommentsKey.formatted(postId, offset);
    }

    private String getCommentFeedStartFromKey(Long postId) {
        return postCommentFeedOffsetKey.formatted(postId);
    }
}
