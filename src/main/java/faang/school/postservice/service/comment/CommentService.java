package faang.school.postservice.service.comment;

import faang.school.postservice.entity.dto.comment.CommentDto;
import faang.school.postservice.entity.dto.comment.CommentToCreateDto;
import faang.school.postservice.entity.dto.comment.CommentToUpdateDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(long postId, long userId, CommentToCreateDto commentDto);

    List<CommentDto> getAllPostComments(long postId);

    CommentDto updateComment(long commentId, long userId, CommentToUpdateDto commentDto);

    CommentDto deleteComment(long postId, long commentId, long userId);

    CommentDto getById(Long commentId);
}
