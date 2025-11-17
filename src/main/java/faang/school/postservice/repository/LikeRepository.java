package faang.school.postservice.repository;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Like;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like, Long> {
    @Transactional
    void deleteByPostIdAndUserId(long postId, long userId);

    @Transactional
    void deleteByCommentIdAndUserId(long commentId, long userId);

    default Like findByPostIdAndUserIdOrThrow(long postId, long userId) {
        return findByPostIdAndUserId(postId, userId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Like not found for post %d and user %d ", postId, userId))
        );
    }

    default Like findByCommentIdAndUserIdOrThrow(long commentId, long userId) {
        return findByCommentIdAndUserId(commentId, userId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Like not found for post %d and user %d ", commentId, userId))
        );
    }

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);
}


