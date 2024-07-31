package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import feign.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    List<Comment> findAllByPostIdOrderByCreatedAtDesc(long postId);

    @Query("SELECT c FROM Comment c WHERE c.updatedAt >= c.verifiedDate OR c.verifiedDate IS NULL")
    List<Comment> findUnVerifiedComments();

    @Modifying
    @Transactional
    @Query(value = "UPDATE comment SET verified = :verified, verified_date = :verifiedDate WHERE id = :id", nativeQuery = true)
    void updateVerifiedAndVerifiedDate(@Param("id") long id, @Param("verified") boolean verified, @Param("verifiedDate") LocalDateTime verifiedDate);

    @Query(nativeQuery = true, value = "SELECT c.* FROM comment c WHERE c.post_id = :postId ORDER BY c.created_at desc LIMIT :commentLimit")
    List<Comment> findLastLimitComment(Long postId, int commentLimit);

    @Query(nativeQuery = true, value = "SELECT c.created_at FROM Comment c WHERE c.id = :commentId")
    Timestamp getUpdatedTime(Long commentId);
}
