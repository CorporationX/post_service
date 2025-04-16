package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query("SELECT c FROM Comment c WHERE c.verified IS NULL AND c.id IN (?1)")
    List<Comment> getUnverifiedComments(List<Long> ids);

    @Query("SELECT c.id FROM Comment c WHERE  c.verified IS NULL")
    List<Long> getUnverifiedCommentsIds();
}
