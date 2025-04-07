package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommentService {

    long createComment(CommentCreateDto commentCreateDto);

    void updateCommentContent(long commentId, CommentUpdateDto commentUpdateDto);

    ResponseEntity<List<CommentResponseDto>> getAllComments(long postId);

    void deleteComment(long commentId);
}
