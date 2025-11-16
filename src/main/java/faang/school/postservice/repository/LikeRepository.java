package faang.school.postservice.repository;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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


