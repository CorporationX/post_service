package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long postId);

    @Query(value = """
            SELECT *
            FROM comment
            WHERE post_id = :postId
            ORDER BY created_at DESC, id DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Comment> findLatestByPostId(@Param("postId") long postId,
                                     @Param("limit") int limit);
}
