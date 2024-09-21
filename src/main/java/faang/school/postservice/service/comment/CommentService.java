package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(CommentDto comment);

    void updateComment(long id, String content);

    List<CommentDto> getCommentsByPostId(long postId);

    boolean deleteComment(long id);
}
