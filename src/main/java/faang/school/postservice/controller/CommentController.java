package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;

import java.util.List;

public class CommentController {

    private CommentService commentService;

    void createComment(long id, CommentDto commentDto) {
        commentService.createComment(id, commentDto);
    }

    void updateComment(long id, CommentDto commentDto) {
        commentService.updateComment(id, commentDto);
    }

    List<CommentDto> getAllComments(long id) {
        return commentService.getAllComments(id);
    }

    void deleteComment(long id, CommentDto commentDto) {
        commentService.deleteComment(id, commentDto);
    }
}
