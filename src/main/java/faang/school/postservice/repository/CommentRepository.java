package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query(nativeQuery = true, value = "SELECT c.* FROM comment c WHERE c.post_id = :postId ORDER BY c.created_at desc LIMIT :commentLimit")
    List<Comment> findLastLimitComment(Long postId, int commentLimit);

    @Query(nativeQuery = true, value = "SELECT c.created_at FROM Comment c WHERE c.id = :commentId")
    Timestamp getUpdatedTime(Long commentId);
}
