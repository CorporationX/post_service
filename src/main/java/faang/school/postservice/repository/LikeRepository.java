package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("DELETE FROM Like l WHERE l.post.id = :postId AND l.userId = :userId")
    void deleteByPostIdAndUserId(Long postId, Long userId);

    @Query("DELETE FROM Like l WHERE l.comment.id = :commentId AND l.userId = :userId")
    void deleteByCommentIdAndUserId(Long commentId, Long userId);
}