package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto create(long userId, CommentDto dto);

    CommentDto update(CommentDto dto);

    List<CommentDto> findAll(Long postId);

    void delete(Long id);
}