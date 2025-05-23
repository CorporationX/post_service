package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {

    public CommentDto create(CommentDto commentDto);

    public CommentDto update(CommentDto commentDto);

    public CommentDto findById(long commentId);

    public void deleteById(long commentId);

    List<CommentDto> findByPostId(long postId);
}
