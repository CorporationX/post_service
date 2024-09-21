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
    @Query(nativeQuery = true, value = "UPDATE Comment SET content = :content WHERE id = :id")
    void updateContentById(long id, String content);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE Comment SET updated_at = :updateDate WHERE id = :id")
    void updateDateById(long id, LocalDateTime updateDate);

    List<Comment> getByPostIdOrderByCreatedAtDesc(long postId);
}