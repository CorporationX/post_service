package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    List<Comment> findAllByPostIdOrderByCreatedAtDesc(long postId);

    @Query(nativeQuery = true, value = """
            SELECT c.* FROM comment c
            WHERE c.post_id = :postId
            ORDER BY c.created_at DESC
            LIMIT :limit
            """)
    List<Comment> findNewByPostId(long postId, int limit);
}
