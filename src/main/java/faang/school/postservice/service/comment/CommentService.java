package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.SaveCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(Long postId, Long authorId, SaveCommentDto saveCommentDto);
    CommentDto update(Long postId, Long commentId, Long authorId, SaveCommentDto dto);
    List<CommentDto> getByPostId(Long postId);
    void delete(Long postId, Long commentId, Long userId);
}
