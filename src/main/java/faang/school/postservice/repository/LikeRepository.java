package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like, Long> {
    @Transactional
    void deleteByPostIdAndUserId(long postId, long userId);

    @Transactional
    void deleteByCommentIdAndUserId(long commentId, long userId);

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);

    boolean existsByPostIdAndUserId(long postId, long userId);

    boolean existsByCommentIdAndUserId(long commentId, long userId);
}
