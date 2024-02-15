package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/{postId}")
    public ResourceDto createResource(@PathVariable("postId") long postId,
                                      @RequestParam(value = "file") MultipartFile file) {
        return resourceService.createResource(postId, file);
    }

    @GetMapping("/{resourceId}")
    public ResourceDto getResource(@PathVariable long resourceId) {
        return resourceService.getResource(resourceId);
    }

    @GetMapping("/{resourceId}/file")
    public ResponseEntity<byte[]> getFile(@PathVariable long resourceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        byte[] image = resourceService.downloadResource(resourceId);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{resourceId}")
    public ResourceDto deleteResource(@PathVariable long resourceId) {
        return resourceService.deleteResource(resourceId);
    }
}