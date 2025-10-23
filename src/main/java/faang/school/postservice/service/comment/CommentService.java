package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;

import java.util.List;

public interface CommentService {

    ResponseCommentDto createComment(CreateCommentDto createCommentDto);

    ResponseCommentDto updateComment(UpdateCommentDto updateCommentDto);

    List<ResponseCommentDto> getComments(long postId, int page, int pageSize);

    void deleteComment(long commentId);
}