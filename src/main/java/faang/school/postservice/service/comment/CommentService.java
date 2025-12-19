package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.request.RequestCreateComment;
import faang.school.postservice.dto.comment.request.RequestUpdateComment;
import faang.school.postservice.dto.comment.response.ResponseComment;

import java.util.List;

public interface CommentService {

    ResponseComment createComment(RequestCreateComment commentDto, Long postId);

    ResponseComment updateComment(Long postId, Long idComment, RequestUpdateComment commentDto);

    void deleteComment(Long postId, Long id);

    List<ResponseComment> getAllCommentsByPostId(Long postId);
}