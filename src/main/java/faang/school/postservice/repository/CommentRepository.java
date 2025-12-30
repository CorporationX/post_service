package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    Page<Comment> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

    @Query(value = "SELECT DISTINCT author_id FROM comment WHERE author_id IS NOT NULL", nativeQuery = true)
    List<Long> findAllDistinctAuthorIds();

    @Query("SELECT c FROM Comment c WHERE c.post.id IN :postIds")
    List<Comment> findAllByPostIdIn(@Param("postIds") List<Long> postIds);
}
