package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.ResourceService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("post/resource")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("{postId}/files")
    public List<ResourceDto> addResourcesForPost(@PathVariable @Min(1) long postId,
                                                 @Size(max = 10) List<MultipartFile> files) {
        return resourceService.addResources(postId, files);
    }

    @DeleteMapping("{postId}/files/{resourceId}")
    public void deleteResource(@PathVariable @Min(1) long postId,
                               @PathVariable @Min(1) long resourceId) {
        resourceService.deleteResource(postId, resourceId);
    }
}
