package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(Long postId, CreateCommentDto commentDto, Long userId);

    CommentDto updateComment(Long userId, UpdateCommentDto CommentDto);

    List<CommentDto> getCommentsByPostId(Long postId);

    void deleteComment(Long commentID);

}