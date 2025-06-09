package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.service.comment.CommentImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/comments/{commentId}/images")
@RequiredArgsConstructor
public class CommentImageController {

    private final CommentImageService commentImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageResponseDto> uploadImage(@PathVariable Long commentId,
                                                        @RequestParam("file") MultipartFile file) {
        ImageResponseDto response = commentImageService.uploadImageForComment(commentId, file);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{imageId}/download")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long commentId,
                                                  @PathVariable Long imageId) {
        ImageDownloadDto dto = commentImageService.downloadImageByCommentId(commentId, imageId);
        MediaType mediaType = MediaType.parseMediaType(dto.getContentType());
        String fileName = "image_" + imageId + getFileExtension(dto.getContentType());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(dto.getInputStream()));
    }

    @GetMapping("/{imageId}/preview")
    public ResponseEntity<Resource> downloadPreview(@PathVariable Long commentId,
                                                    @PathVariable Long imageId) {
        ImageDownloadDto dto = commentImageService.downloadPreviewByCommentId(commentId, imageId);
        MediaType mediaType = MediaType.parseMediaType(dto.getContentType());
        String fileName = "preview_" + imageId + getFileExtension(dto.getContentType());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(dto.getInputStream()));
    }

    @GetMapping("/{imageId}/view")
    public ResponseEntity<Resource> viewImage(@PathVariable Long commentId,
                                              @PathVariable Long imageId) {
        ImageDownloadDto dto = commentImageService.downloadImageByCommentId(commentId, imageId);
        MediaType mediaType = MediaType.parseMediaType(dto.getContentType());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(new InputStreamResource(dto.getInputStream()));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long commentId,
                                            @PathVariable Long imageId) {
            commentImageService.deleteImage(commentId, imageId);

            return ResponseEntity.noContent().build();
    }

    private String getFileExtension(String contentType) {
        if (contentType == null) return "";
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/gif"  -> ".gif";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }
}
