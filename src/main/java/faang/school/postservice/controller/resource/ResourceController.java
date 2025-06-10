package faang.school.postservice.controller.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
        Resource resource = resourceService.addBuildForPost(postId, file);
        ResourceDto resourceDto = ResourceMapper.resourceToResourceDto(resource);
        return ResponseEntity.ok(resourceDto);
    }

    @DeleteMapping("/{postId}/resource/{resourceId}")
    public ResponseEntity<Void> deleteImageByPostId(@PathVariable("postId") long postId,
                                                    @PathVariable("resourceId") long resourceId) {
        resourceService.deleteImageByPostId(postId, resourceId);
        return ResponseEntity.noContent().build();
    }
}
