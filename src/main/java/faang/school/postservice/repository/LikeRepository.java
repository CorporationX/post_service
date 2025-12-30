package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    @Transactional
    void deleteByPostIdAndUserId(long postId, long userId);

    @Transactional
    void deleteByCommentIdAndUserId(long commentId, long userId);

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);

    Optional<List<Like>> findAllByPostId(long postId);

    @Query(value = "SELECT DISTINCT user_id FROM likes WHERE user_id IS NOT NULL", nativeQuery = true)
    List<Long> findAllDistinctUserIds();

    @Query("SELECT l FROM Like l WHERE l.post.id IN :postIds")
    List<Like> findAllByPostIdIn(@Param("postIds") List<Long> postIds);
}
