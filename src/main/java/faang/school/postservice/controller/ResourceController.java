package faang.school.postservice.controller;


import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.resource.ResourceServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceServiceImpl resourceService;

    @PostMapping(value = "/post/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ResourceDto>> uploadResources(
            @PathVariable Long postId,
            @RequestParam("files") List<MultipartFile> files) {
        log.info("Uploading {} files for post ID: {}", files.size(), postId);

        List<ResourceDto> dtos = resourceService.uploadResourcesForPost(files, postId);

        log.info("Successfully uploaded {} files for post ID: {}", dtos.size(), postId);
        return ResponseEntity.ok(dtos);
    }

    @PutMapping(value = "/post/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateResources(
            @PathVariable Long postId,
            @RequestParam(value = "newFiles", required = false) List<MultipartFile> newFiles,
            @RequestParam(value = "filesToDelete", required = false) List<Long> filesToDelete) {
        log.info("Updating resources for post ID: {}, new files: {}, files to delete: {}",
                postId, newFiles != null ? newFiles.size() : 0,
                filesToDelete != null ? filesToDelete.size() : 0);

        resourceService.updatePostResources(postId, newFiles, filesToDelete);

        log.info("Successfully updated resources for post ID: {}", postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ResourceDto>> getResources(@PathVariable Long postId) {
        log.info("Getting resources for post ID: {}", postId);

        List<ResourceDto> dtos = resourceService.getResourcesByPostId(postId);

        log.info("Found {} resources for post ID: {}", dtos.size(), postId);
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deleteResources(@PathVariable Long postId) {
        log.info("Deleting resources for post ID: {}", postId);

        resourceService.deletePostResources(postId);

        log.info("All resources by Post {} was deleted", postId);
        return ResponseEntity.ok().build();
    }
}
