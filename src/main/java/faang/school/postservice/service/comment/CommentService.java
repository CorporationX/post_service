package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;

import java.util.List;

public interface CommentService {
    void banUsersForComments();

    void createComment(CommentRequestDto commentRequestDto);

    void updateComment(Long id, CommentUpdateDto commentUpdateDto);

    List<CommentResponseDto> getCommentsByPostId(Long postId);

    void deleteComment(Long id);
}
