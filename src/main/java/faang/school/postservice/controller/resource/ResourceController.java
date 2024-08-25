package faang.school.postservice.controller.resource;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/{postId}/file")
    public ResourceDto addResource(@PathVariable long postId, MultipartFile file) {
        return resourceService.addResource(postId, file);
    }

    @DeleteMapping("/{postId}/files/{resourceId}")
    public void deleteResource(@PathVariable long postId, @PathVariable long resourceId) {
        resourceService.deleteResource(postId, resourceId);
    }

    @PostMapping("/{postId}/files")
    public List<ResourceDto> addResources(@PathVariable long postId,  List<MultipartFile> files){
        return resourceService.addResources(postId, files);
    }

    @GetMapping("/{postId}/{resourceID}")
    public InputStream downloadFile(@PathVariable long postId, @PathVariable long resourceId){
        return resourceService.downloadFile(postId, resourceId);
    }
}
