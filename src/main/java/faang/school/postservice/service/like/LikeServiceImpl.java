package faang.school.postservice.service.like;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService{

    private final LikeRepository likeRepository;
    private final LikeValidator likeValidator;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public void likePost(long userId, long postId) {
        likeValidator.ensureUserExists(userId);
        likeValidator.ensureLikeOnPostAbsent(postId, userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found, id: " + postId));
        Like like = createLikeOnPost(userId, post);
        post.getLikes().add(like);
        postRepository.save(post);

        likeRepository.save(like);
        log.info("Like on post was created, id: {}", like.getId());
    }

    @Override
    public void likeComment(long userId, long commentId) {
        likeValidator.ensureUserExists(userId);
        likeValidator.ensureLikeOnCommentAbsent(commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found, id: " + commentId));
        Like like = createLikeOnComment(userId, comment);
        comment.getLikes().add(like);
        commentRepository.save(comment);

        likeRepository.save(like);
        log.info("Like on comment was created, id: {}", like.getId());
    }

    @Override
    public void deleteLikeFromPost(long userId, long postId) {
        likeValidator.ensureUserExists(userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found, id: " + postId));
        Like like = likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Like not found, postId: %s, userId: %s %n", postId, userId))
                );
        post.getLikes().remove(like);
        postRepository.save(post);

        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Like on post was deleted, postId: {}, userId: {}", postId, userId);
    }

    @Override
    public void deleteLikeFromComment(long userId, long commentId) {
        likeValidator.ensureUserExists(userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found, id: " + commentId));
        Like like = likeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Like not found, commentId: %s, userId: %s %n", commentId, userId))
                );
        comment.getLikes().remove(like);
        commentRepository.save(comment);

        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("Like on comment was deleted, commentId: {}, userId: {}", commentId, userId);
    }

    private Like createLikeOnPost(long userId, Post post) {
        return Like.builder()
                .userId(userId)
                .post(post)
                .build();
    }

    private static Like createLikeOnComment(long userId, Comment comment) {
        return Like.builder()
                .userId(userId)
                .comment(comment)
                .build();
    }
}
