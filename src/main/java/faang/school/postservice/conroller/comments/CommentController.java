package faang.school.postservice.conroller.comments;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comments.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public CommentDto createComment(
            @PathVariable @Min(1) Long postId,
            @RequestBody @Valid CommentDto commentDto) {
        commentDto.setPostId(postId);
        return commentService.createComment(commentDto);
    }

    @GetMapping("posts/{postId}/comments")
    public List<CommentDto> getCommentsByPostId(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @PutMapping("/posts/{commentId}/comments")
    public CommentDto updateComment(@PathVariable Long commentId,
                                    @RequestBody @Valid CommentDto commentDto) {
        return commentService.updateComment(commentId, commentDto);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
