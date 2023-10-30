package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    List<Comment> findByVerifiedFalse();

    List<Comment> findByVerifiedTrue();

    List<Comment> findByVerifiedDateBeforeAndVerifiedFalse(LocalDateTime date);

    List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long postId);

}
