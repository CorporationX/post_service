package faang.school.postservice.service;

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

    void commentersBanCheck(int unverifiedCommentsLimit);

    void delete(Long id);
}