package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"post", "post.ad"})
    @Query("SELECT c FROM Comment c WHERE c.verified = false OR (c.verified = true AND c.updatedAt > c.verifiedDate)")
    Page<Comment> findCommentsForModeration(Pageable pageable);

    //TODO N+1 PROBLEM
}
