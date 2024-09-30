package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(CommentDto comment);

    void updateComment(long id, UpdateCommentDto updateCommentDto);

    List<CommentDto> getCommentsByPostId(long postId);

    void deleteComment(long id);

    void checkAllComment();
}
