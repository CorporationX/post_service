package faang.school.postservice.controller.resource;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/{postId}/files")
    public ResourceDto addFile(@PathVariable long postId, @RequestPart(value = "file") MultipartFile file) {
        return resourceService.addFile(postId, file);
    }
}
