package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like, Long> {
    void deleteByPostIdAndUserId(long postId, long userId);

    void deleteByCommentIdAndUserId(long commentId, long userId);

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);

    Page<Like> getLikesByPostId(Long postId, Pageable pageable);

    boolean existsLikesByPostId(Long postId);

    boolean existsLikesByCommentId(Long commentId);

    Page<Like> getLikesByCommentId(Long commentId, Pageable pageable);
}
