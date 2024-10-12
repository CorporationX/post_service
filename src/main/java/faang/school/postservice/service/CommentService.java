package faang.school.postservice.service;

import faang.school.postservice.model.dto.comment.CommentRequestDto;
import faang.school.postservice.model.dto.comment.CommentResponseDto;

import java.util.List;

public interface CommentService {

    CommentResponseDto create(long userId, CommentRequestDto dto);

    CommentResponseDto update(CommentRequestDto dto);

    List<CommentResponseDto> findAll(Long postId);

    void delete(Long id);
}