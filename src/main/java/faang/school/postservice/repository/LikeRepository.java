package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("DELETE FROM Like l WHERE l.post.id = :postId AND l.userId = :userId")
    @Modifying
    void deleteByPostIdAndUserId(Long postId, Long userId);

    @Query("DELETE FROM Like l WHERE l.comment.id = :commentId AND l.userId = :userId")
    @Modifying
    void deleteByCommentIdAndUserId(Long commentId, Long userId);

    @Query("SELECT l FROM Like l WHERE l.post.id = :postId AND l.userId = :userId")
    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    @Query("SELECT l FROM Like l WHERE l.comment.id = :commentId AND l.userId = :userId")
    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);
}
