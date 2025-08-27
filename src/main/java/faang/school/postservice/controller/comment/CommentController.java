package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentViewDto> create(
            @PathVariable Long postId,
            @RequestBody @Valid CommentCreateDto createDto) {
        CommentViewDto createdComment = commentService.create(createDto, postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentViewDto> update(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateDto updateDto) {
        CommentViewDto updatedComment = commentService.update(postId, commentId, updateDto);
        return ResponseEntity.ok(updatedComment);
    }

    @GetMapping
    public ResponseEntity<List<CommentViewDto>> getAllCommentsByPostId(
            @PathVariable Long postId) {
        List<CommentViewDto> comments = commentService.getAllCommentByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        commentService.delete(postId, commentId);
        return ResponseEntity.noContent().build();
    }
}
