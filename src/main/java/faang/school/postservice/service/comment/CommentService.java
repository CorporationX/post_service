package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentFiltersDto;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommentService {
    CommentResponseDto createComment(CommentRequestDto commentDto);

    CommentResponseDto updateComment(long commentId, CommentUpdateDto commentUpdateDto);

    List<CommentResponseDto> getComments(CommentFiltersDto commentFiltersDto);

    void deleteComment(long commentId);

    void uploadImage(Long commentId, MultipartFile file);

    void verifyComments();

    void publishUsersToBanEvent();

    List<CommentResponseDto> getAllByPostId(long postId);
}
