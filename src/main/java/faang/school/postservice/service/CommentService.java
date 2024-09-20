package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(CommentDto commentDto);

    CommentDto updateComment(CommentDto commentDto);

    List<CommentDto> getAllCommentsByPostId(long postId);

    void deleteComment(long commentId);
}
