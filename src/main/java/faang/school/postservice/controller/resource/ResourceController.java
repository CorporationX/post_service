package faang.school.postservice.controller.resource;

import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/post")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/{postId}/image")
    public String addResource(@PathVariable long postId,  @RequestPart(value = "file")  MultipartFile file) throws IOException {
        resourceService.addResource(postId, file);
        return "File uploaded successfully";
    }
}
