package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadImageResource(@PathVariable("id") Long id) {
            return resourceService.downloadResource(id);
    }

    @PostMapping("/{post_id}")
    public ResourceDto addResource(@PathVariable Long post_id, @RequestBody MultipartFile file) {
        return resourceService.addResource(post_id, file);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok("");
    }
}
