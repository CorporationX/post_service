package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CommentService {
    Mono<Void> moderateComments();

    void createComment(CommentRequestDto commentRequestDto);

    void updateComment(Long id, CommentUpdateDto commentUpdateDto);

    List<CommentResponseDto> getCommentsByPostId(Long postId);

    void banUsersForComments();

    void deleteComment(Long id);
}
