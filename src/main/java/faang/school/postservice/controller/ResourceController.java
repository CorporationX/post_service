package faang.school.postservice.controller;

import faang.school.postservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/{postId}/resources")
    public String addResource(@PathVariable Long postId, @RequestParam("file") MultipartFile file) {
        resourceService.addResource(postId, file);
        return "File uploaded successfully";
    }

    @DeleteMapping("/{postId}/resources/{resourceId}")
    public String deleteResource(@PathVariable Long postId, @PathVariable Long resourceId) {
        resourceService.deleteResource(postId, resourceId);
        return "File deleted successfully";
    }
}
