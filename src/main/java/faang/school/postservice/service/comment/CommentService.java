package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto);

    CommentDto updateCommentContent(long id, CommentDto commentDto);

    List<CommentDto> getAllComments(CommentDto commentDto);

    void deleteComment(long id);
}
