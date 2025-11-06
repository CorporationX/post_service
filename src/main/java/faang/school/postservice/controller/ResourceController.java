package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    @PutMapping("/{projectId}/add")
    public ResourceDto addResource(@PathVariable Long projectId, @RequestBody MultipartFile file) {
        return resourceService.addResource(projectId, file);
    }
}
