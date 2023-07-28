package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.util.validator.comment.CommentControllerValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;
    private final CommentControllerValidator validator;

    @PostMapping("/create")
    public CommentDto createComment(@RequestBody @Valid CommentDto commentDto,
                                    BindingResult bindingResult) {
        validator.checkBindingResult(bindingResult);
        return commentService.createComment(commentDto);
    }

    @GetMapping("/post/{postId}")
    public List<CommentDto> getCommentsByPostId(@PathVariable("postId") long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/delete/{commentId}")
    public boolean deleteComment(@PathVariable("commentId") long commentId) {
        return commentService.deleteCommentById(commentId);
    }
}
