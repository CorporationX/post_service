package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.mapper.comment.MapperComment;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceFacade {
    private final MapperComment mapperComment;
    private final CommentService commentService;

    public CommentDtoResponse createComment(CommentCreateDto commentDto) {
        Comment comment = mapperComment.fromCreatDtoToEntity(commentDto);
        Comment commentCreate = commentService.createComment(comment);

        return mapperComment.fromEntityToDto(commentCreate);
    }

    public CommentDtoResponse updateComment(CommentUpdateDto commentDto) {
        Comment comment = mapperComment.fromUpdateDtoToEntity(commentDto);
        Comment commentUpdate = commentService.updateComment(comment);

        return mapperComment.fromEntityToDto(commentUpdate);
    }

    public List<CommentDtoResponse> getAllComments(long postId) {
        List<Comment> comments = commentService.getAllComments(postId);

        return comments.stream()
                .map(mapperComment::fromEntityToDto)
                .toList();
    }

    public void deleteComment(long commentId) {
        commentService.deleteComment(commentId);
    }
}
