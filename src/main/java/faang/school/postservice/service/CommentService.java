package faang.school.postservice.service;

import faang.school.postservice.model.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto, Long userId);

    List<CommentDto> getComment(Long postId);

    void deleteComment(Long commentId);

    CommentDto updateComment(Long commentId, CommentDto commentDto, Long userId);
}
