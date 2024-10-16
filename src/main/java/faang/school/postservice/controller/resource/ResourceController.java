package faang.school.postservice.controller.resource;

import faang.school.postservice.model.dto.resource.ResourceDto;
import faang.school.postservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
@Tag(name = "Resource Controller", description = "Controller used for adding/deleting files for posts")
public class ResourceController {
    private final ResourceService resourceService;

    @Operation(description = "Method for attaching images to a post")
    @PostMapping("/posts/{postId}/images")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ResourceDto> attachImages(@PathVariable @Positive Long postId,
                                          @RequestBody @NonNull List<MultipartFile> files) {
        return resourceService.attachImages(postId, files);
    }

    @Operation(description = "Method for setting image status to 'DELETED'")
    @DeleteMapping("/{resourceId}")
    public ResourceDto deleteResource(@PathVariable @Positive Long resourceId) {
        return resourceService.deleteResource(resourceId);
    }

    @Operation(description = "Method for setting image status to 'ACTIVE'")
    @PutMapping("/{resourceId}")
    public ResourceDto restoreResource(@PathVariable @Positive Long resourceId) {
        return resourceService.restoreResource(resourceId);
    }
}
