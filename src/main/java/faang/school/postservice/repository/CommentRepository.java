package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query("SELECT c FROM Comment c WHERE c.verifiedDate = NULL OR (c.updatedAt >= :lastDayDate AND c.verifiedDate <= :lastDayDate)")
    List<Comment> findNotCheckedToVerificationComments(@Param("lastDayDate") LocalDateTime lastDayDate);
}
