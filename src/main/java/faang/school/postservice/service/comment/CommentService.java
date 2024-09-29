package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.SortingStrategyDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long postId, CommentDto commentDto);

    CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto);

    List<CommentDto> getComments(Long postId, SortingStrategyDto sortingStrategyDto);

    CommentDto deleteComment(Long postId, Long commentId);
}
