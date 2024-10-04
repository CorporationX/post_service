package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService {

    CommentResponseDto create(long userId, CommentRequestDto dto);

    CommentResponseDto update(CommentRequestDto dto);

    List<CommentResponseDto> findAll(Long postId);

    List<Comment> collectUnverifiedComments();

    Map<Long, Long> groupUnverifiedCommentAuthors(List<Comment> unverifiedComments);

    void delete(Long id);
}