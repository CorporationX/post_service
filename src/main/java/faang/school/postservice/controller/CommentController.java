package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable
                                                                @NotNull
                                                                @Positive(message = "Post ID must be positive")
                                                                Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok(commentService.createComment(commentDto));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable
                                                    @NotNull
                                                    @Positive(message = "Comment ID must be positive")
                                                    Long commentId,
                                                    @RequestHeader("x-user-id")
                                                    @NotNull
                                                    @Positive(message = "User ID must be positive")
                                                    Long userId,
                                                    @RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok(commentService.updateComment(commentId, commentDto, userId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable
                                              @NotNull
                                              @Positive(message = "Comment ID must be positive")
                                              Long commentId,
                                              @RequestHeader("x-user-id")
                                              @NotNull
                                              @Positive(message = "User ID must be positive")
                                              Long userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}