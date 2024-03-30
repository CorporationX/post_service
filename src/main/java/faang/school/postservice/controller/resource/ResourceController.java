package faang.school.postservice.controller.resource;

import faang.school.postservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @Operation(summary = "Delete resource")
    @DeleteMapping("/{resourceId}")
    public void deleteResource(@PathVariable Long resourceId) {
        resourceService.deleteResource(resourceId);
    }
}
