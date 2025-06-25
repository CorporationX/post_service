package faang.school.postservice.repository;

import faang.school.postservice.model.AuthorCommentCount;
import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);
    List<Comment> findByIdIn(List<Long> ids);

    @Query(nativeQuery = true, value = """
            SELECT author_id as authorId, COUNT(*) FROM comment c
            LEFT JOIN users u ON u.id = c.author_id
            WHERE u.banned = false AND c.verified = false
            GROUP BY author_id
            """)
    List<AuthorCommentCount> findNotVerifiedComments();
}
