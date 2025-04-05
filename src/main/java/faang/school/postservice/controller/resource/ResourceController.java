package faang.school.postservice.controller.resource;

import faang.school.postservice.dto.resource.ResourceRequest;
import faang.school.postservice.dto.resource.ResourceResponse;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("{postId}/addFiles")
    public List<ResourceResponse> addFilesToPost(@PathVariable long postId, @RequestBody List<MultipartFile> files) {
        return resourceService.addFilesToPost(postId, files);
    }

    @PostMapping("{postId}/addFile")
    public ResourceResponse addFileToPost(@PathVariable long postId, @RequestBody MultipartFile file) {
        return resourceService.addFileToPost(postId, file);
    }

    @DeleteMapping("/removeFile")
    public void removeFileFromPost(@RequestBody ResourceRequest resourceRequest) {
        resourceService.removeFileFromPost(resourceRequest);
    }

    @GetMapping("{postId}/getFiles")
    public List<byte[]> getFilesPost(@PathVariable long postId) {
        return resourceService.getFilesForPost(postId);
    }
}

