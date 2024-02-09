package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validation.comment.CommentValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentValidation commentValidation;


    @PostMapping
    private void addNewComment(Long id, CommentDto comment) {
        commentValidation.validateCommentData(comment);
        commentService.addNewComment(id, comment);
    }

    @PutMapping
    private void changeComment(Long id, CommentDto comment) {
        commentValidation.validateCommentData(comment);
        commentService.changeComment(id, comment);
    }

    @PutMapping
    private void deleteComment(Long id, CommentDto comment) {
        commentService.deleteComment(id, comment);
    }

    @GetMapping
    private void getAllComments(Long id) {
        commentService.getAllComments(id);
    }
}
