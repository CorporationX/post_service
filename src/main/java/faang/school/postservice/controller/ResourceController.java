package faang.school.postservice.controller;

import faang.school.postservice.dto.s3.ResourceDto;
import faang.school.postservice.service.ResourceService;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/posts/{postId}/resources")
    public ResourceDto addResource(@PathVariable Long postId,@RequestBody MultipartFile file) {
        return resourceService.addResource(postId, file);
    }
}
