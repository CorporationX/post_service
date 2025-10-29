package faang.school.postservice.controller.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.resource.ResourceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<ResourceDto> uploadResources(@RequestParam Long postId,
                                          @RequestParam("files") List<MultipartFile> files) {
        log.info("upload images");
        List<ResourceDto> resources = resourceService.uploadResources(postId, files);
        log.info("Successfully uploaded");
        return resources;
    }

    @GetMapping
    public List<ResourceDto> getResourcesByPostId(@RequestParam Long postId) {
        log.info("get post resources");
        return resourceService.getResourcesByPostId(postId);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable @Positive Long resourceId) {
        log.info("delete resource");
        resourceService.deleteResource(resourceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{resourceId}/download")
    public ResponseEntity<byte[]> downloadResource(@PathVariable @Positive Long resourceId) {
        log.info("download resource");
        return resourceService.downloadResource(resourceId);
    }
}
