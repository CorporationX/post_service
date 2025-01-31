package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like, Long> {

    Optional<Like> findLikeByPostIdAndUserId(long userId, long postId);

    Optional<Like> findLikeByCommentIdAndUserId(long userId, long commentId);
}
