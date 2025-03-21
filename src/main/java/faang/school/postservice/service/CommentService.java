package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import java.util.List;

public interface CommentService {
    CommentDto createComment(Long postId, CommentDto commentDto);

    CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto);

    List<CommentDto> getCommentsByPostId(Long postId);

    void deleteComment(Long postId, Long commentId);
}