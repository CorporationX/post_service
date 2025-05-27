package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.mapper.comment.MapperComment;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceFacade {
    //todo доработать

    private final MapperComment mapperComment;
    private final CommentService commentService;

    public void createComment(CreateCommentDto commentDto) {
        Comment comment = mapperComment.fromCreatDtoToEntity(commentDto);
        commentService.createComment(comment);
    }

    public void updateComment(UpdateCommentDto commentDto) {
        Comment comment = mapperComment.fromUpdateDtoToEntity(commentDto);
        commentService.updateComment(comment);
    }

    public List<CommentDtoResponse> getAllComment(long postId) {
        List<Comment> comments = commentService.getAllComment(postId);

        return comments.stream()
                .map(mapperComment::fromEntityToModifiedDto)
                .toList();
    }

    public void deleteComment(long commentId) {
        commentService.deleteComment(commentId);
    }
}
