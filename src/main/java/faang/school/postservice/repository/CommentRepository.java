package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM comment
             WHERE post_id = :postId
             ORDER BY created_at DESC
             """)
    List<Comment> findAllByPostIdOrderByCreatedAtDesc(long postId);

    @Query(value = """
        SELECT c.authorId
        FROM Comment c
        WHERE c.verified = false
        GROUP BY c.authorId
        HAVING COUNT(c) > 5""")
    List<Long> findAuthorIdsToBeBanned();
}
