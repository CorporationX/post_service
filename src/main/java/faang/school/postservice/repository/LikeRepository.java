package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends CrudRepository<Like, Long> {

    void deleteByPostIdAndUserId(long postId, long userId);

    void deleteByCommentIdAndUserId(long commentId, long userId);

    List<Like> findByPostId(long postId);

    List<Like> findByCommentId(long commentId);

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);

    @Query(nativeQuery = true, value = "SELECT count(l.id) FROM Likes l WHERE l.post_id = :postId")
    Long getNumberOfLikeByPostId(Long postId);
}
