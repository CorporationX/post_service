package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final UserContext userContext;
    private final CommentService commentService;

    @PostMapping("/comments/{id}")
    public CommentDto addComment(
            @PathVariable Long postId,
            @RequestBody CreateCommentDto commentDto
    ) {
        return commentService.addComment(postId, commentDto);
    }

    @PutMapping("/comments/")
    public CommentDto updateComment(
            @RequestBody UpdateCommentDto updateDto
    ) {
        return commentService.updateComment(userContext.getUserId(), updateDto);
    }

    @GetMapping("/comments/{id}")
    public List<CommentDto> getCommentsByPostId(
            @PathVariable Long postId
    ) {
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/comments/delete/{id}")
    public void deleteComment(
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(commentId);
    }
}