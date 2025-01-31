package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeRepositoryAdapter {
    private final LikeRepository likeRepository;

    public Like findLikeByPostIdAndUserId(long userId, long postId) {
        return likeRepository.findLikeByPostIdAndUserId(userId, postId)
                .orElse(null);
    }

    public Like findLikeByCommentIdAndUserId(long userId, long commentId) {
        return likeRepository.findLikeByCommentIdAndUserId(userId, commentId)
                .orElse(null);
    }
}
