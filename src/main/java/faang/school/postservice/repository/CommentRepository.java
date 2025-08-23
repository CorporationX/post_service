package faang.school.postservice.repository;

import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query("""
            SELECT new faang.school.postservice.dto.feed.CommentFeedDto(
                c.post.id,
                c.content,
                c.authorId,
                c.createdAt
            )
            FROM Comment c
            WHERE c.post.id = :postId
            ORDER BY c.createdAt DESC
            LIMIT :limit
            """)
    List<CommentFeedDto> findLastCommentsForPost(long postId, int limit);
}
