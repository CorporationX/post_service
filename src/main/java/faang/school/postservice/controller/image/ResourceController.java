package faang.school.postservice.controller.image;

import faang.school.postservice.model.Resource;
import faang.school.postservice.service.image.ResourceService;
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
@RequestMapping("/posts/{postId}/resources")
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<Resource> uploadImages(@PathVariable Long postId,
                                       @RequestParam("files") List<MultipartFile> files) {
        log.info("upload images");
        return resourceService.uploadImages(postId, files);
    }

    @GetMapping
    public List<Resource> getResourcesByPostId(@PathVariable long postId) {
        log.info("get post resources");
        return resourceService.getResourcesByPostId(postId);
    }

    @DeleteMapping("/{resourceId}")
    public List<Resource> deleteResource(@PathVariable @Positive long postId,
                                         @PathVariable @Positive long resourceId) {
        log.info("delete resource");
        return resourceService.deleteResource(postId, resourceId);
    }

    @GetMapping("/{resourceId}/download")
    public ResponseEntity<byte[]> downloadResource(@PathVariable Long postId,
                                                   @PathVariable Long resourceId
    ) {
        log.info("download resource");
        return resourceService.downloadResource(postId, resourceId);
    }
}
