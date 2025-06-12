package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.enums.ImageRequestMode;
import faang.school.postservice.service.comment.CommentImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @GetMapping("/{imageId}/view")
    public ResponseEntity<Resource> viewImage(@PathVariable Long commentId,
                                              @PathVariable Long imageId,
                                              @RequestParam(defaultValue = "ORIGINAL") ImageRequestMode mode) {
        ImageDownloadDto dto = switch (mode) {
            case ORIGINAL -> commentImageService.downloadImageByCommentId(commentId, imageId);
            case PREVIEW -> commentImageService.downloadPreviewByCommentId(commentId, imageId);
        };

        return ResponseEntity.ok()
                .contentType(dto.getContentType())
                .body(dto.getResource());
    }

    @GetMapping("/{imageId}/download")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long commentId,
                                                  @PathVariable Long imageId,
                                                  @RequestParam(defaultValue = "ORIGINAL") ImageRequestMode mode) {
        ImageDownloadDto dto = switch (mode) {
            case ORIGINAL -> commentImageService.downloadImageByCommentId(commentId, imageId);
            case PREVIEW -> commentImageService.downloadPreviewByCommentId(commentId, imageId);
        };

        return ResponseEntity.ok()
                .contentType(dto.getContentType())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + dto.getOriginalFileName() + "\"")
                .body(dto.getResource());
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long commentId,
                                            @PathVariable Long imageId) {
        commentImageService.deleteImage(commentId, imageId);

        return ResponseEntity.noContent().build();
    }
}
