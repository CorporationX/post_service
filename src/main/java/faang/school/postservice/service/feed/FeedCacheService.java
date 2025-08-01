package faang.school.postservice.service.feed;

import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.properties.RedisTtlProperties;
import faang.school.postservice.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCacheService {

    private final RedisService redisService;
    private final RedisTtlProperties ttlProperties;


    @Value(value = "${feed.comment-limit:5}")
    private int commentLimit;
    @Value(value = "${feed.post-limit:500}")
    private int feedLimit;
    @Value(value = "${feed.userPrefix:user}")
    private String userPrefix;
    @Value(value = "${feed.postPrefix:post}")
    private String postPrefix;
    @Value(value = "${feed.feedPostPrefix:feed_post}")
    private String feedPostPrefix;
    @Value(value = "${feed.feedCommentPrefix:feed_comment}")
    private String feedCommentPrefix;
    @Value(value = "${feed.commentPrefix:comment}")
    private String commentPrefix;
    @Value(value = "${feed.viewPrefix:view}")
    private String viewPrefix;
    @Value(value = "${feed.likePrefix:like}")
    private String likePrefix;

    public boolean checkUserSaved(Long userId) {
        String userKey = redisService.getKey(userPrefix, userId);
        return redisService.keyPresent(userKey);
    }

    public void saveUser(UserDto user) {
        redisService.saveAsString(
                redisService.getKey(userPrefix, user.getId()),
                user,
                ttlProperties.getDefaultDays(), TimeUnit.DAYS);
    }

    public UserDto getUser(Long userId) {
        return redisService.getWithUpdateTtlAs(
                redisService.getKey(userPrefix, userId),
                ttlProperties.getDefaultDays(),
                TimeUnit.DAYS,
                UserDto.class
        );
    }

    public void savePost(ResponsePostDto post) {
        redisService.saveAsString(
                redisService.getKey(postPrefix, post.getPostId()),
                post,
                ttlProperties.getDefaultDays(),
                TimeUnit.DAYS
        );

    }

    public ResponsePostDto getPost(Long postId) {
        return redisService.getWithUpdateTtlAs(
                redisService.getKey(postPrefix, postId),
                ttlProperties.getDefaultDays(),
                TimeUnit.DAYS,
                ResponsePostDto.class
        );
    }

    public void saveComment(CommentDtoResponse comment) {
        redisService.saveAsString(
                redisService.getKey(commentPrefix, comment.getCommentId()),
                comment,
                ttlProperties.getDefaultDays(),
                TimeUnit.DAYS
        );
    }

    public CommentDtoResponse getComment(CommentDtoResponse comment) {
        return redisService.getWithUpdateTtlAs(
                redisService.getKey(commentPrefix, comment.getPostId()),
                ttlProperties.getDefaultDays(),
                TimeUnit.DAYS,
                CommentDtoResponse.class
        );
    }

    public void savePostToFeed(Long userId, Long postId, LocalDateTime createdAt) {
        long score = createdAt.toEpochSecond(ZoneOffset.UTC);
        redisService.saveWithUpdateTtlZSetWithLockAndLimit(
                redisService.getKey(feedPostPrefix, userId),
                postId.toString(),
                score,
                feedLimit
        );
    }

    public void saveCommentToFeed(Long postId, Long commentId, LocalDateTime createdAt) {
        long score = createdAt.toEpochSecond(ZoneOffset.UTC);
        redisService.saveWithUpdateTtlZSetWithLockAndLimit(
                redisService.getKey(feedCommentPrefix, postId),
                commentId.toString(),
                score,
                commentLimit
        );
    }

    public long getViews(Long postId) {
        return redisService.getCounterByKey(redisService.getKey(viewPrefix, postId));
    }

    public void incrementViews(Long postId) {
        redisService.incrementByKey(redisService.getKey(viewPrefix, postId));
    }

    public long getLikes(Long postId) {
        return redisService.getCounterByKey(redisService.getKey(likePrefix, postId));
    }

    public void incrementLikes(Long postId) {
        redisService.incrementByKey(redisService.getKey(likePrefix, postId));
    }
}