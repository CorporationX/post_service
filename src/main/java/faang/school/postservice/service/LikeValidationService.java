package faang.school.postservice.service;

import faang.school.postservice.exception.AlreadyLikedException;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikeValidationService {

    private final LikeRepository likeRepository;

    public void validatePostAlreadyLiked(Long userId, Long postId) {
        if(likeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new AlreadyLikedException("Post already liked by user");
        }
    }

    public void validatePostNotBeenLiked(Long userId, Long postId) {
        if(!likeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new PostNotFoundException("Post has not been liked by user");
        }
    }

    public void validateLikeTarget(Long postId, Long commentId) {
        if (postId != null && commentId != null) {
            throw new IllegalArgumentException("Cannot like both post and comment simultaneously");
        }
        if (postId == null && commentId == null) {
            throw new IllegalArgumentException("Target for like must be specified (post or comment)");
        }
    }

    public void validateCommentAlreadyLiked(Long userId, Long commentId) {
        if(likeRepository.existsByUserIdAndCommentId(userId, commentId)) {
            throw new AlreadyLikedException("Comment already liked by user");
        }
    }

    public void validateCommentNotBeenLiked(Long userId, Long commentId) {
        if(!likeRepository.existsByUserIdAndCommentId(userId, commentId)) {
            throw new CommentNotFoundException("Comment has not been liked by user");
        }
    }
}