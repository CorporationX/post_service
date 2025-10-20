package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.Request.RequestCommentDto;
import faang.school.postservice.dto.comment.Response.ResponseCommentDto;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.model.Post;

import java.util.List;

public interface CommentService {

    ResponseCommentDto createComment(RequestCommentDto commentDto, Long postId) throws ResourceNotFoundException;

    ResponseCommentDto updateComment(Long postId, Long idComment, RequestCommentDto commentDto) throws ResourceNotFoundException;

    void deleteComment(Long postId, Long id) throws ResourceNotFoundException;

    List<ResponseCommentDto> getAllCommentsByPostId(Long postId) throws ResourceNotFoundException;
}