package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query("SELECT c FROM Comment c WHERE c.verified = false and (c.verifiedDate is null or c.verifiedDate >= :startDate)")
    List<Comment> findUnverifiedComments(@Param("startDate") LocalDateTime startDate);

    @Query(""" 
            SELECT c.authorId 
            FROM Comment c  
            WHERE c.verified = false AND c.verifiedDate IS NOT NULL  
            GROUP BY c.authorId 
            HAVING COUNT(c.id) > :banCommentLimit""")
    List<Long> findUserIdsToBan(@Param("banCommentLimit") int banCommentLimit);
}
