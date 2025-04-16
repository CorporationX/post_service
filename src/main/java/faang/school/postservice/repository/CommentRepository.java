package faang.school.postservice.repository;

import faang.school.postservice.dto.comment.UserUnverifiedCommentsDto;
import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query("SELECT new faang.school.postservice.dto.comment.UserUnverifiedCommentsDto(c.authorId, COUNT(c)) " +
            "FROM Comment c WHERE c.verified = false GROUP BY c.authorId")
    List<UserUnverifiedCommentsDto> getUnverifiedCommentAuthorCountDto();
}
