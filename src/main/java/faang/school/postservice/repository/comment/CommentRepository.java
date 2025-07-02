package faang.school.postservice.repository.comment;

import faang.school.postservice.entity.comment.Comment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Modifying
    @Query(value = """
        SELECT * FROM comment
        WHERE verified = false
        ORDER BY created_at
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
    List<Comment> lockAndFetchUnverified(@Param("limit") int limit);
}
