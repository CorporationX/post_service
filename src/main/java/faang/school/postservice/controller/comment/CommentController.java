package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.image.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createCommentWithImage(
            @RequestParam("content") @NotBlank String content,
            @RequestParam("authorId") @NotNull @Positive Long authorId,
            @RequestParam("postId")   @NotNull @Positive Long postId,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        validateImage(file);
        CommentDto saved = commentService.createCommentWithOptionalImage(
                content, authorId, postId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable @NotNull @Positive Long id) {
        CommentDto dto = commentService.getCommentById(id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable @NotNull @Positive Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "File size exceeds 5 MB"
            );
        }
        String ct = file.getContentType();
        if (ct == null || !ct.toLowerCase().startsWith("image/")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "File is not an image"
            );
        }
    }
}
