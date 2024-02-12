package faang.school.postservice.controller.resource;

import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/post")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/{postId}/image")
    public String addResource(@PathVariable long postId, @RequestPart(value = "file") MultipartFile file) {
        resourceService.addResource(postId, file);
        return String.format("File %s uploaded successfully", file.getName());
    }

    @DeleteMapping("/{postId}/image/{imageId}")
    public String deleteResource(@PathVariable long postId, @PathVariable long imageId) {
        resourceService.deleteResource(postId, imageId);
        return "File deleted";
    }
}
