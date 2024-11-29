package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE Comment SET content = :content, updated_at = :updateDate WHERE id = :id")
    void updateContentAndDateById(long id, String content, LocalDateTime updateDate);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    List<Comment> getByPostIdOrderByCreatedAtDesc(long postId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM comment c
            WHERE c.post_id = :postId
            ORDER BY c.created_at DESC
            LIMIT :limit
            """)
    List<Comment> getByPostIdWithLimit(long postId, long limit);

}