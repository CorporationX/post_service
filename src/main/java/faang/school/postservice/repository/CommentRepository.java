package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM Comment c
            WHERE c.verified_date is NULL
            FOR UPDATE SKIP LOCKED
            LIMIT 10000
            """)
    List<Comment> findAllNotVerified();

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM Comment WHERE verified = false")
    int deleteAllFailedVerification();
}
