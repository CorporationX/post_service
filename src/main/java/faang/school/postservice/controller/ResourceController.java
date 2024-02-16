package faang.school.postservice.controller;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
@Validated
public class ResourceController {
    private final ResourceService resourceService;
    private final PostService postService;

    @PutMapping("/{postId}/add")
    public ResourceDto addResource(@PathVariable Long postId, @RequestBody MultipartFile file) {
        return resourceService.addResource(postId, file);
    }

    @GetMapping(path = "/{resourceId}", produces = "application/octet-stream")
    public ResponseEntity<byte[]> downloadResource(@PathVariable Long resourceId) {
        byte[] imageBytes = null;
        try {
            imageBytes = resourceService.downloadResource(resourceId).readAllBytes();
        } catch (IOException e) {
            e.getMessage();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

//    @DeleteMapping("/{resourceId}")
//    public ResponseEntity<String> deleteResource(@PathVariable Long resourceId) {
//        resourceService.deleteResource(resourceId)
//    }

}
