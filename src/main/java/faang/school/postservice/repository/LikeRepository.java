package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    void deleteByPostIdAndUserId(Long postId, Long userId);

    void deleteByCommentIdAndUserId(Long commentId, Long userId);

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);

    @Query("SELECT l.userId FROM Like l WHERE l.post.id = :postId")
    List<Long> findUserIdsByPostId(long postId);

    @Query("SELECT l.userId FROM Like l WHERE l.comment.id = :commentId")
    List<Long> findUserIdsByCommentId(long commentId);
}
