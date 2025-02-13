package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query(nativeQuery = true, value = """
            SELECT author_id 
            FROM comment 
            WHERE verified = false AND verified_date IS NOT NULL
            GROUP BY author_id
            HAVING COUNT(*) >= :unverifiedCommentsCountForBan
            """
    )
    List<Long> findAuthorsForBanWithUnverifiedCommentsCount(int unverifiedCommentsCountForBan);

    @Query("SELECT c FROM Comment c WHERE c.verified IS NULL")
    List<Comment> findUnverifiedComments();
}
