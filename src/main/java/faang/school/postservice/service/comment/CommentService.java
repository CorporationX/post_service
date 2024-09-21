package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(CommentDto comment);

    CommentDto updateComment(Long id, String content);

    List<CommentDto> getCommentsByPostId(Long postId);

    void deleteComment(Long id);
}
