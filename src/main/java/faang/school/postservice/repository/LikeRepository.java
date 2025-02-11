package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.repository.CrudRepository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like, Long> {

    Optional<Like> findLikeByPostIdAndUserId(long userId, long postId);

    Optional<Like> findLikeByCommentIdAndUserId(long userId, long commentId);

    List<Like> findLikesByPostId(long postId, Pageable pageable);

    List<Like> findLikesByCommentId(long postId);
}
