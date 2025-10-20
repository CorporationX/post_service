package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final UserContext userContext;
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long postId,
            @RequestBody CreateCommentDto commentDto
    ) {
        CommentDto createdComment = commentService.addComment(postId, commentDto);
        return ResponseEntity.ok(createdComment);
    }

    @PutMapping
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long postId,
            @RequestBody UpdateCommentDto updateDto
    ) {
        Long userId = userContext.getUserId();
        CommentDto updated = commentService.updateComment(userId, updateDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(
            @PathVariable Long postId
    ) {
        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}