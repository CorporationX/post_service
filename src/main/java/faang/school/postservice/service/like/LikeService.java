package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final UserContext context;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;

    public void addToPost(long postId) {
        long currentUserId = context.getUserId();
        log.info("Start adding like to post {} by user {}", postId, currentUserId);

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Post " + postId + " not found"));
        userServiceClient.getUser(currentUserId);

        checkPossibilityLikePost(currentUserId, post);
        Like like = createLike(currentUserId, post, null);

        like = likeRepository.save(like);
        log.info("Post {} successfully liked by user {}. Like id - {}", postId, currentUserId, like.getId());
    }

    public void addToComment(long commentId) {
        long currentUserId = context.getUserId();
        log.info("Start adding like to comment {} by user {}", commentId, currentUserId);

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Comment " + commentId + " not found"));
        userServiceClient.getUser(currentUserId);

        checkPossibilityLikeCommentByUser(currentUserId, comment);
        Like like = createLike(currentUserId, null, comment);

        like = likeRepository.save(like);
        log.info("Comment {} successfully liked by user {}. Like id - {}", commentId, currentUserId, like.getId());
    }

    public void deleteFromPost(long postId) {
        long currentUserId = context.getUserId();
        log.info("Start deleting like from post {} by user {}", postId, currentUserId);
        likeRepository.deleteByPostIdAndUserId(postId, currentUserId);
        log.info("Like successfully deleted from post {} by user {}", postId, currentUserId);
    }

    public void deleteFromComment(long commentId) {
        long currentUserId = context.getUserId();
        log.info("Start deleting like from comment {} by user {}", commentId, currentUserId);
        likeRepository.deleteByCommentIdAndUserId(commentId, currentUserId);
        log.info("Comment successfully liked from comment {} by user {}", commentId, currentUserId);
    }

    private void checkPossibilityLikePost(long currentUserId, Post post) {
        checkPostIsNotDeleted(post);
        checkNotExistsLikedPostByUser(currentUserId, post.getId());
        checkNotExistsLikedCommentForPostByUser(currentUserId, post.getId());
    }

    private void checkPostIsNotDeleted(Post post) {
        if (post.isDeleted()) {
            throw new IllegalStateException(String.format(
                    "Post %d already deleted", post.getId()));
        }
    }

    private void checkNotExistsLikedPostByUser(long currentUserId, Long postId) {
        likeRepository.findByPostIdAndUserId(postId, currentUserId)
                .ifPresent(like -> {
                    throw new IllegalStateException(String.format(
                            "User %d already liked post %d", currentUserId, postId));
                });
    }

    private void checkNotExistsLikedCommentForPostByUser(long currentUserId, long postId) {
        boolean existsLikedComment = likeRepository.existsByUserIdAndCommentPostId(currentUserId, postId);
        if (existsLikedComment) {
            throw new IllegalStateException(String.format(
                    "User %d already liked comment for post %d", currentUserId, postId));
        }
    }

    private void checkPossibilityLikeCommentByUser(long currentUserId, Comment comment) {
        chackNotExistsLikedCommentByUser(currentUserId, comment.getId());
        checkNotExistsLikedPostByUser(currentUserId, comment.getPost().getId());
    }

    private void chackNotExistsLikedCommentByUser(long currentUserId, Long commentId) {
        likeRepository.findByCommentIdAndUserId(commentId, currentUserId)
                .ifPresent(like -> {
                    throw new IllegalStateException(String.format(
                            "User %d already liked comment %d", currentUserId, commentId));
                });
    }

    private Like createLike(long currentUserId, Post post, Comment comment) {
        return Like.builder()
                .userId(currentUserId)
                .post(post)
                .comment(comment)
                .build();
    }
}
