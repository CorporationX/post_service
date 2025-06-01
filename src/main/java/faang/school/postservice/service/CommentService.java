package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentForCreationDto;
import faang.school.postservice.dto.comment.CommentForUpdateDto;
import faang.school.postservice.dto.comment.CommentOutputDto;

import java.util.List;

public interface CommentService {

    CommentOutputDto createComment(CommentForCreationDto commentDto);

    CommentOutputDto updateComment(CommentForUpdateDto commentDto);

    CommentOutputDto findCommentById(long commentId);

    void deleteCommentById(long commentId);

    List<CommentOutputDto> findCommentByPostId(long postId);
}
