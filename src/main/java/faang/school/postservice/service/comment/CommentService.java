package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;

import java.util.List;

public interface CommentService {

    CommentDto create(Long postId, CommentDto commentDto);

    CommentDto update(Long commentId, CommentUpdateDto commentUpdateDto);

    List<CommentDto> getByPostId(Long postId);

    void delete(Long commentId);
}
