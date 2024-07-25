package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long postId, CommentDto commentDto);
    CommentDto updateComment(Long commentId, CommentDto commentDto);
    List<CommentDto> getCommentsByPostId(Long postId);
    void deleteComment(Long commentId);
    boolean existsById(long id);
}
