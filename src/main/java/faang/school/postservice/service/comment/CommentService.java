package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(Long postId, CommentDto commentDto);

    CommentDto updateComment(Long commentId, CommentUpdateDto commentUpdateDto);

    List<CommentDto> getComments(Long postId);

    void deleteComment(Long commentId);


}
