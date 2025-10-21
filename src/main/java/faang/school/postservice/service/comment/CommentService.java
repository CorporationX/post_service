package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.SendCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;

import java.util.List;

public interface CommentService {

    void sendComment(SendCommentDto sendCommentDto);

    void updateComment(UpdateCommentDto updateCommentDto, long postId);

    List<ResponseCommentDto> getComments(long postId, int page, int pageSize);

    void deleteComment(long commentId, long postId);
}