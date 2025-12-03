package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CommentService {

    CommentDto createComment(CommentDto commentDto);

    CommentDto updateComment(Long commentId, CommentDto commentDto);

    void deleteComment(Long commentId);

    CommentDto getCommentById(Long commentId);

    Page<CommentDto> getCommentsByPostId(Long postId, Pageable pageable);
}
