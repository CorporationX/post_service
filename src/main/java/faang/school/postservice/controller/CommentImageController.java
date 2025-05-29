package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.service.comment.CommentImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments/{commentId}/images")
@Tag(name = "Comment Image Management", description = "Endpoints for managing comment images")
public class CommentImageController {

    private final CommentImageService commentImageService;

    @Operation(
             summary = "Upload image to comment",
            description = "Uploads and attaches an image to the specified comment"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommentViewDto> uploadImage(
            @Parameter(description = "ID of the post containing the comment", required = true, example = "1")
            @PathVariable @NotNull Long postId,
            @Parameter(description = "ID of the comment to attach image to", required = true, example = "1")
            @PathVariable @NotNull Long commentId,
            @Parameter(description = "Image file to upload (max 5MB)", required = true)
            @RequestPart("file") @Valid @Size(max = 5 * 1024 * 1024) MultipartFile file) {

        log.info("Request to upload image for comment ID: {} in post ID: {}", commentId, postId);
        CommentViewDto updatedComment = commentImageService.uploadImage(postId, commentId, file);

        return ResponseEntity.ok(updatedComment);
    }

    @Operation(
            summary = "Delete comment image",
            description = "Deletes the image attached to the specified comment"
    )
    @DeleteMapping
    public ResponseEntity<CommentViewDto> deleteImage(
            @Parameter(description = "ID of the post containing the comment", required = true, example = "1")
            @PathVariable @NotNull Long postId,
            @Parameter(description = "ID of the comment to remove image from", required = true, example = "1")
            @PathVariable @NotNull Long commentId) {

        log.info("Request to delete image for comment ID: {} in post ID: {}", commentId, postId);
        CommentViewDto updatedComment = commentImageService.deleteImage(postId, commentId);

        return ResponseEntity.ok(updatedComment);
    }
}