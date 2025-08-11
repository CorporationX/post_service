package faang.school.postservice.repository;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    List<Comment> findAllByPostIdOrderByCreatedAtDesc(long postId);

    default Comment getRequiredById(long id) {
        return findById(id).orElseThrow(() ->
                new EntityNotFoundException("Comment with id " + id + " not found"));
    }
}
