package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.image.CommentImageDto;
import faang.school.postservice.service.comment.CommentImageService;
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

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentImageController {

    private final CommentImageService commentService;

    @PostMapping
    public ResponseEntity<CommentImageDto> createCommentWithImage(
            @RequestParam("content") @NotBlank String content,
            @RequestParam("authorId") @NotNull @Positive Long authorId,
            @RequestParam("postId") @NotNull @Positive Long postId,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        validateImage(file);
        CommentImageDto saved = commentService.createCommentWithOptionalImage(
                content, authorId, postId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentImageDto> getCommentWithImage(@PathVariable @NotNull @Positive Long id) {
        CommentImageDto dto = commentService.getCommentById(id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommentWithImage(@PathVariable @NotNull @Positive Long id) {
        commentService.deleteCommentWithImage(id);
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
