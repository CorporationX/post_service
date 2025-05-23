package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;

public interface CommentService {

    public CommentDto create(long creatorId, CommentDto commentDto);

    public CommentDto update(CommentDto commentDto);

    public CommentDto findById(long commentId);

    public void deleteById(long commentId);
}
