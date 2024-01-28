package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.CommentEditDto;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class CommentController {
    private final CommentService commentService;
    private final CommentValidator commentValidator;

    @PostMapping("/{postId}/comments")
    public CommentDto createComment(@PathVariable Long postId, @RequestBody CommentDto commentDto) {
        commentValidator.validateIdIsNotNull(postId);
        commentValidator.validateComment(commentDto);
        return commentService.createComment(postId, commentDto);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long postId, @PathVariable Long commentId,
                                    @RequestBody CommentEditDto commentEditDto) {
        commentValidator.validateIdIsNotNull(postId);
        commentValidator.validateIdIsNotNull(commentId);
        commentValidator.validateComment(commentEditDto);
        return commentService.updateComment(postId, commentId, commentEditDto);
    }

    @GetMapping("/{postId}/comments")
    public List<CommentDto> getCommentsByPostId(@PathVariable Long postId) {
        commentValidator.validateIdIsNotNull(postId);
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public void deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        commentValidator.validateIdIsNotNull(postId);
        commentValidator.validateIdIsNotNull(commentId);
        commentService.deleteComment(postId, commentId);
    }
}
