package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query(""" 
            SELECT DISTINCT c.authorId
            FROM Comment c
            WHERE c.verified = false
            AND (
                SELECT COUNT(*)
                FROM Comment c2
                WHERE c2.authorId = c.authorId AND c2.verified = false
              ) >= :banSize
            """)
    List<Long> findAllUsersForBan(@Param("banSize") int banSize);
}
