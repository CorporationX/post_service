package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.ResourceService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("api/v1/post")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/{postId}/file")
    public List<ResourceDto> addResource(@PathVariable @Positive(message = "Id must be greater than zero") long postId,
                                         @RequestPart(value = "file") @Size(max = 10) List<MultipartFile> files) {
        return resourceService.addResource(postId, files);

    }

    @DeleteMapping("/{postId}/file/{fileId}")
    public String deleteResource(@PathVariable @Positive(message = "Id must be greater than zero") long postId,
                                 @PathVariable @Positive(message = "Id must be greater than zero") long fileId) {
        resourceService.deleteResource(postId, fileId);
        return "File deleted";
    }
}
