package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {
    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;

    @PutMapping("{post-id}/files")
    public ResourceDto addFileToPost(@PathVariable("post-id") Long postId,
                                     @RequestParam MultipartFile file) {
        Resource resource = resourceService.addFileToPost(file, postId);
        return resourceMapper.toResourceDto(resource);
    }

    @PutMapping("/files/update")
    public ResourceDto updateFileInPost(@RequestParam MultipartFile file,
                                        @RequestParam("resource-id") Long resourceId) {
        Resource updatedResource = resourceService.updateFileInPost(file, resourceId);
        return resourceMapper.toResourceDto(updatedResource);
    }

    @PutMapping("/files/remove")
    public void removeFileInPost(@RequestParam("resource-id") Long resourceId) {
        resourceService.removeFileInPost(resourceId);
    }
}
