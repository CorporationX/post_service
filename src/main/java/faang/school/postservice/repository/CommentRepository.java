package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM comment
            WHERE post_id = :postId
            ORDER BY id DESC
            LIMIT :batchSize
            """)
    List<Comment> findLastBatchByPostId(int batchSize, long postId);

    @Query(nativeQuery = true, value = """
            SELECT c.* FROM comment c
            WHERE c.id IN (SELECT c1.id
                           FROM comment c1
                           WHERE c1.post_id = c.post_id
                           ORDER BY c1.id DESC
                           LIMIT :batchSize)
            AND post_id IN (:postIds)
            """)
    List<Comment> findLastBatchByPostIds(int batchSize, List<Long> postIds);
}
