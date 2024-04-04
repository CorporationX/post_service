package faang.school.postservice.controller.resource;

import faang.school.postservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    @Operation(summary = "Delete resource")
    @DeleteMapping("/{resourceId}")
    public void deleteResource(@PathVariable @Positive(message = "Resource id must be positive number") Long resourceId) {
        resourceService.deleteResource(resourceId);
    }
}
