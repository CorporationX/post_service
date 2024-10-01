package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping("/{resource_id}")
    public ResponseEntity<byte[]> downloadResource(@PathVariable("resource_id") Long id) {
        InputStream inputStream = resourceService.downloadResource(id);
        byte[] imageBytes;

        try {
            imageBytes = inputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/{post_id}")
    public ResourceDto addResource(@PathVariable Long post_id, @RequestBody MultipartFile file) {
        return resourceService.addResource(post_id, file);
    }

    @DeleteMapping("/{resource_id}")
    public ResponseEntity<String> deleteResource(@PathVariable Long resource_id) {
        resourceService.deleteResource(resource_id);
        return ResponseEntity.ok("Resource deleted successfully");
    }
}
