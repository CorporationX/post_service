package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.LikeException;
import faang.school.postservice.like.LikeDto;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    public static final String ALREADY_LIKED = "You have already liked this.";
    public static final String POST_NOT_FOUND = "Post not found with id: %d";
    public static final String COMMENT_NOT_FOUND = "Comment not found with id: %d";
    public static final String USER_NOT_FOUND = "User not found with id: %d";
    public static final String BOTH_LIKE = "You cannot like a post and comment at the same time.";


    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final LikeMapper likeMapper;

    @Transactional
    public LikeDto likePost(long postId, long userId) {
        log.info("User {} liked post {} !", userId, postId);
        validateUser(userId);
        Post post = getPost(postId);
        validateLikesRepeat(post, null);
        validateNotLiked(postId, userId, true);

        Like like = buildLike(userId, post, null);
        return likeMapper.toDto(likeRepository.save(like));
    }

    @Transactional
    public void unlikePost(long postId, long userId) {
        log.info("User {} removed a like from a post {}", userId, postId);
        validateUser(userId);
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Transactional
    public LikeDto likeComment(long commentId, long userId) {
        log.info("User {} liked comment {} !", userId, commentId);
        validateUser(userId);
        Comment comment = getComment(commentId);
        validateLikesRepeat(null, comment);
        validateNotLiked(commentId, userId, false);

        Like like = buildLike(userId, null, comment);
        return likeMapper.toDto(likeRepository.save(like));
    }

    @Transactional
    public void unlikeComment(long commentId, long userId) {
        validateUser(userId);
        log.info("User {} removed a like from a comment {}", userId, commentId);
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    public Like buildLike(long userId, Post post, Comment comment) {
        validateLikesRepeat(post, comment);
        return Like.builder()
                .userId(userId)
                .post(post)
                .comment(comment)
                .build();
    }

    private Post getPost(long postId) {
        String error = String.format(POST_NOT_FOUND, postId);
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error(error);
                    return new LikeException(error);
                });
    }

    private Comment getComment(long commentId) {
        String error = String.format(COMMENT_NOT_FOUND, commentId);
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error(error);
                    return new LikeException(error);
                });
    }

    private void validateNotLiked(long targetId, long userId, boolean isPost) {
        boolean exist = isPost
                ? likeRepository.findByPostIdAndUserId(targetId, userId).isPresent()
                : likeRepository.findByCommentIdAndUserId(targetId, userId).isPresent();

        if (exist) {
            log.error(ALREADY_LIKED);
            throw new LikeException(ALREADY_LIKED);
        }
    }

    private void validateUser(long userId) {
        String error = String.format(USER_NOT_FOUND, userId);

        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            log.error(error);
            throw new LikeException(error);
        }
    }

    private void validateLikesRepeat(Post post, Comment comment) {
        if (post != null && comment != null) {
            throw new LikeException(BOTH_LIKE);
        }
    }
}
