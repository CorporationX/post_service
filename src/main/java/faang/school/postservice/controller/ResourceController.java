package faang.school.postservice.controller;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.service.resource.ResourceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/post/{postId}")
    public ResourceDto addImage(
            @PathVariable Long postId, @RequestBody @NonNull MultipartFile file) {
        return resourceService.addImage(postId, file);
    }

    @PostMapping("/post/list/{postId}")
    public List<ResourceDto> addImages(
            @PathVariable Long postId, @RequestBody @NonNull List<MultipartFile> files) {
        return resourceService.addImages(postId, files);
    }

    @DeleteMapping("/{resourceId}")
    public ResourceDto deleteResource(@PathVariable Long resourceId) {
        return resourceService.deleteResource(resourceId);
    }
}
