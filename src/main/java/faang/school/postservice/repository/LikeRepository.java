package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends CrudRepository<Like, Long> {

    Optional<Like> deleteByPostIdAndUserId(long postId, long userId);

    Optional<Like> deleteByCommentIdAndUserId(long commentId, long userId);

    List<Like> findByPostId(long postId);

    List<Like> findByCommentId(long commentId);

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    boolean existsByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);

    boolean existsByCommentIdAndUserId(long commentId, long userId);
}
