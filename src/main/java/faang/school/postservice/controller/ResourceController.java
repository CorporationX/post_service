package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.inner.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping
    public List<ResourceDto> addResourcesForPost(long postId, List<MultipartFile> files) {
        return resourceService.addResources(postId, files);
    }

    @DeleteMapping
    public void deleteResource(long postId, long resourceId) {
        resourceService.deleteResource(postId, resourceId);
    }
}
