package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceResponseDto;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping(value = "/{resourceId}")
    public ResponseEntity<byte[]> downloadResource(@PathVariable Long resourceId) {
        return resourceService.downloadResource(resourceId);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ResourceResponseDto> uploadResource(
            @PathVariable Long postId, @RequestBody MultipartFile file) {
        return resourceService.uploadResource(postId, file);
    }

    @PutMapping("/images/{postId}")
    public ResponseEntity<List<ResourceResponseDto>> uploadImageResource(
            @PathVariable Long postId, @RequestBody MultipartFile file) {
        return resourceService.uploadImageResource(postId, file);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long resourceId) {
        return resourceService.deleteResource(resourceId);
    }
}
