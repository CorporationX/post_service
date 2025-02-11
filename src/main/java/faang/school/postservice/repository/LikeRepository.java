package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like, Long> {

    Optional<Like> findLikeByPostIdAndUserId(long userId, long postId);

    Optional<Like> findLikeByCommentIdAndUserId(long userId, long commentId);

    Slice<Like> findLikesByPostId(long postId, Pageable pageable);

    Slice<Like> findLikesByCommentId(long postId, Pageable pageable);
}
