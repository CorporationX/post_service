package faang.school.postservice.repository;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    Page<Comment> findAllByPostId(Long postId, Pageable pageable);


    @Query(value = """
            SELECT * FROM comment 
            WHERE is_verified IS NULL 
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<Comment> findCommentsByVerifiedIsNull();

    default Comment findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found %d".formatted(id)));
    }
}
