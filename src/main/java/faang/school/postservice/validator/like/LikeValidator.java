package faang.school.postservice.validator.like;

import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {

    private final LikeRepository likeRepository;

    public void validateLikeForPostExists(Long postId, Long userId) {
        likeRepository.findByPostIdAndUserId(postId, userId).ifPresent(like -> {
            throw new IllegalArgumentException("Like already exists for this post. postId: " + postId + ", userId: " + userId + ".");
        });
    }

    public void validateLikeForCommentExists(Long commentId, Long userId) {
        likeRepository.findByCommentIdAndUserId(commentId, userId).ifPresent(like -> {
            throw new IllegalArgumentException("Like already exists for this comment. commentId: " + commentId + ", userId: " + userId + ".");
        });
    }
}