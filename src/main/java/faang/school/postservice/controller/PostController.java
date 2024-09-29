package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.ResourceService;
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
    public ResourceDto addFileToPost(@RequestParam("file") MultipartFile file,
                                     @PathVariable("post-id") Long postId) {
        Resource resource = resourceService.addFileToPost(file, postId);
        return resourceMapper.toResourceDto(resource);
    }

    @PutMapping("{post-id}/files/update")
    public ResourceDto updateFileInPost(@RequestParam("file") MultipartFile file,
                                        @PathVariable("post-id") Long postId,
                                        @RequestParam("resource-id") Long resourceId) {
        Resource updatedResource = resourceService.updateFileInPost(file, resourceId, postId);
        return resourceMapper.toResourceDto(updatedResource);
    }

    @PutMapping("{post-id}/files/remove")
    public void removeFileInPost(@PathVariable("post-id") Long postId,
                                 @RequestParam("resource-id") Long resourceId) {
        resourceService.removeFileInPost(resourceId, postId);
    }
}
