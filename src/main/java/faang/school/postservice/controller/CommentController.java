package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody @Validated CommentCreateDto dto,
                                                    @RequestParam Long userId) {
        return ResponseEntity.ok(commentService.createComment(dto, userId));
    }

    @PutMapping
    public ResponseEntity<CommentDto> updateComment(@RequestBody @Validated CommentUpdateDto dto,
                                                    @RequestParam Long userId) {
        return ResponseEntity.ok(commentService.updateComment(dto, userId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @RequestParam Long userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }
}
