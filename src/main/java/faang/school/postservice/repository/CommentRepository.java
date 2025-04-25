package faang.school.postservice.repository;

import faang.school.postservice.model.AuthorCommentCount;
import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);
    List<Comment> findByIdIn(List<Long> ids);

    @Query(nativeQuery = true, value = """
            select author_id as authorId, count(*) from comment
            where verified = false
            group by author_id
            """)
    List<AuthorCommentCount> findNotVerifiedComments();
}
