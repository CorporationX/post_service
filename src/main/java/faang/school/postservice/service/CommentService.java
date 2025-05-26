package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentForCreationDto;
import faang.school.postservice.dto.comment.CommentForUpdateDto;
import faang.school.postservice.dto.comment.CommentOutputDto;

import java.util.List;

public interface CommentService {

    public CommentOutputDto create(CommentForCreationDto commentDto);

    public CommentOutputDto update(CommentForUpdateDto commentDto);

    public CommentOutputDto findById(long commentId);

    public void deleteById(long commentId);

    List<CommentOutputDto> findByPostId(long postId);
}
