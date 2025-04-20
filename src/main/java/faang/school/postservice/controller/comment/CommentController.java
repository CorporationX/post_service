package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long postId,
            @Valid @ModelAttribute CommentDto commentDto) {
        return ResponseEntity.ok(commentService.createComment(postId, commentDto));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDto commentDto
    ) {
        return ResponseEntity.ok(commentService.updateComment(commentId, commentDto));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getAllComments(postId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        Map<String, String> message = Map.of("message", String.format("Comment with ID %d deleted", id));
        return ResponseEntity.ok(message);
    }
}
