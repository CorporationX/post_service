package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(Long postId, CreateCommentDto createCommentDto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto CommentDto);

    List<CommentDto> getCommentsByPostId(Long postId);

    void deleteComment(Long commentID);

}