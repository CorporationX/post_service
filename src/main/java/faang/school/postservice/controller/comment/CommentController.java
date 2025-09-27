package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    @PostMapping
    public ResponseEntity<CommentViewDto> create(
            @PathVariable Long postId,
            @RequestBody @Valid CommentCreateDto createDto) {
        CommentViewDto createdComment = service.create(createDto, postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentViewDto> update(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateDto updateDto) {
        CommentViewDto updatedComment = service.update(postId, commentId, updateDto);
        return ResponseEntity.ok(updatedComment);
    }

    @GetMapping
    public ResponseEntity<List<CommentViewDto>> getAllCommentsByPostId(
            @PathVariable Long postId) {
        List<CommentViewDto> comments = service.getAllCommentByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        service.delete(postId, commentId);
        return ResponseEntity.noContent().build();
    }
}
