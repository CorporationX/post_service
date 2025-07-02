package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.*;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserContext userContext;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@PathVariable Long postId,
                                                    @RequestBody @Valid CommentCreateDto dto) {
        Long userId = userContext.getUserId();
        CommentDto created = commentService.createComment(postId, dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long postId,
                                                    @PathVariable Long commentId,
                                                    @RequestBody @Valid CommentUpdateDto dto) {
        Long userId = userContext.getUserId();
        CommentDto updated = commentService.updateComment(commentId, dto, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = userContext.getUserId();
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}