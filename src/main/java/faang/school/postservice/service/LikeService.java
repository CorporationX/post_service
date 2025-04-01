package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.LikeException;
import faang.school.postservice.like.LikeDto;
import faang.school.postservice.like.TargetLike;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    public static final String ALREADY_LIKED = "You have already liked this.";
    public static final String POST_NOT_FOUND = "Post not found with id: %d";
    public static final String COMMENT_NOT_FOUND = "Comment not found with id: %d";
    public static final String USER_NOT_FOUND = "User not found with id: %d";
    public static final String LIKE_NOT_FOUND = "No likes found for a comment or post %d from a user with id: %d";
    public static final String BOTH_LIKE = "You cannot like a post and comment at the same time.";
    public static final String ERROR_VALIDATING_USER = "Error occurred when validating a user with id: %d.";

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final LikeMapper likeMapper;
    private final TargetLike targetLike;

    @Transactional
    public LikeDto likePost(long postId, long userId) {
        validateUser(userId);

        Post post = getEntity(() -> postRepository.findById(postId), () -> String.format(POST_NOT_FOUND, postId));

        validateNotLiked(postId, userId, targetLike.POST);

        Like like = buildLike(userId, post, null);
        LikeDto result = likeMapper.toLikeDto(likeRepository.save(like));
        log.info("User {} liked post {} !", userId, postId);
        return result;
    }

    @Transactional
    public void unlikePost(long postId, long userId) {
        validateUser(userId);
        if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {
            String error = (String.format(LIKE_NOT_FOUND, postId, userId));
            log.error(error);
            throw new LikeException(error);
        }
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("User {} removed a like from a post {}", userId, postId);
    }

    @Transactional
    public LikeDto likeComment(long commentId, long userId) {
        validateUser(userId);

        Comment comment = getEntity(() -> commentRepository.findById(commentId), () ->
                String.format(COMMENT_NOT_FOUND, commentId));

        validateLikesRepeat(null, comment);
        validateNotLiked(commentId, userId, targetLike.COMMENT);

        Like like = buildLike(userId, null, comment);
        LikeDto result = likeMapper.toLikeDto(likeRepository.save(like));
        log.info("User {} liked comment {} !", userId, commentId);
        return result;
    }

    @Transactional
    public void unlikeComment(long commentId, long userId) {
        validateUser(userId);
        if (!likeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            String error = (String.format(LIKE_NOT_FOUND, commentId, userId));
            log.error(error);
            throw new LikeException(error);
        }
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("User {} removed a like from a comment {}", userId, commentId);
    }

    public Like buildLike(long userId, Post post, Comment comment) {
        validateLikesRepeat(post, comment);
        return Like.builder()
                .userId(userId)
                .post(post)
                .comment(comment)
                .build();
    }

    private <T> T getEntity(Supplier<Optional<T>> finder, Supplier<String> errorMessage) {
        return finder.get()
                .orElseThrow(() -> {
                    String error = errorMessage.get();
                    log.error(error);
                    return new LikeException(error);
                });
    }

    private void validateNotLiked(long targetId, long userId, TargetLike targetLike) {
        boolean exist = switch (targetLike) {
            case POST -> likeRepository.findByPostIdAndUserId(targetId, userId).isPresent();
            case COMMENT -> likeRepository.findByCommentIdAndUserId(targetId, userId).isPresent();
        };

        if (exist) {
            log.error(ALREADY_LIKED);
            throw new LikeException(ALREADY_LIKED);
        }
    }

    private void validateUser(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            String error = String.format(USER_NOT_FOUND, userId);
            log.error(error);
            throw new LikeException(error);
        } catch (FeignException e) {
            String error = String.format(ERROR_VALIDATING_USER, userId);
            log.error(error, e);
            throw new LikeException(error);
        }
    }

    private void validateLikesRepeat(Post post, Comment comment) {
        if (post != null && comment != null) {
            throw new LikeException(BOTH_LIKE);
        }
        if (post != null && commentRepository.existsById(post.getId())) {
            throw new LikeException(BOTH_LIKE);
        }
    }
}
