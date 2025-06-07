package faang.school.postservice.controller.image;

import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageResponseDto> uploadImage(@RequestParam("file") MultipartFile file) {
        ImageResponseDto response = imageService.uploadImage(file);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long id) {
        var dto = imageService.downloadImageById(id);
        var mediaType = imageService.detectContentType(id);
        String fileName = Paths.get(dto.getContentType()).getFileName().toString();

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(dto.getInputStream()));
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<Resource> downloadPreview(@PathVariable Long id) {
        var dto = imageService.downloadPreviewById(id);
        var mediaType = imageService.detectContentType(id);
        String fileName = Paths.get(dto.getContentType()).getFileName().toString();

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(dto.getInputStream()));
    }

    @GetMapping("/{id}/view")
    public ResponseEntity<Resource> viewImage(@PathVariable Long id) {
        var dto = imageService.downloadImageById(id);
        var mediaType = imageService.detectContentType(id);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(new InputStreamResource(dto.getInputStream()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}
