package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.dto.comment.CommentForCreationDto;
import faang.school.postservice.dto.comment.CommentForUpdateDto;
import faang.school.postservice.dto.comment.CommentOutputDto;

import java.util.List;

public interface CommentService {

    CommentOutputDto createComment(CommentForCreationDto commentDto) throws JsonProcessingException;

    CommentOutputDto updateComment(CommentForUpdateDto commentDto);

    CommentOutputDto findCommentById(long commentId);

    void deleteCommentById(long commentId);

    List<CommentOutputDto> findCommentByPostId(long postId);
}
