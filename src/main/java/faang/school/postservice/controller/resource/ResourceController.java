package faang.school.postservice.controller.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.dto.s3.S3Dto;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping("{postId}")
    public ResponseEntity<ResourceDto> uploadImageToPost(@PathVariable("postId") long postId,
                                                         @RequestParam("image") MultipartFile file) {
        Resource resource = resourceService.addImageToPost(postId, file);
        ResourceDto resourceDto = ResourceMapper.resourceToResourceDto(resource);
        return ResponseEntity.ok(resourceDto);
    }

    @DeleteMapping("/{postId}/resource/{resourceId}")
    public ResponseEntity<Void> deleteImageByPostId(@PathVariable("postId") long postId,
                                                    @PathVariable("resourceId") long resourceId) {
        resourceService.deleteImageByPostId(postId, resourceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/resource/{resourceId}")
    public ResponseEntity<org.springframework.core.io.Resource> getImage(@PathVariable("postId") long postId,
                                                                         @PathVariable("resourceId") long resourceId){
        S3Dto s3Dto = resourceService.downloadImage(postId, resourceId);
        return ResponseEntity.ok()
                .contentLength(s3Dto.getContentLength())
                .contentType(MediaType.parseMediaType(s3Dto.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=" + s3Dto.getName())
                .body(s3Dto.getResource());
    }
}
