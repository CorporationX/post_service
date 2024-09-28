package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;

    @PutMapping("/images")
    public List<ResourceDto> addImagesToPost(@RequestBody List<MultipartFile> files,
                                             @RequestParam("post-id") Long postId) {
        List<Resource> resources = resourceService.addImagesToPost(files, postId);
        return resourceMapper.toResourceDtoList(resources);
    }

    @PutMapping("/images/update")
    public ResourceDto updateImageInPost(@RequestBody MultipartFile file,
                                         @RequestParam("resource-id") Long resourceId,
                                         @RequestParam("post-id") Long postId) {

        Resource updatedResource = resourceService.updateImageInPost(file, resourceId, postId);
        return resourceMapper.toResourceDto(updatedResource);
    }

    @PutMapping("/images/remove")
    public void removeImageInPost(@RequestParam("resource-id") Long resourceId,
                                  @RequestParam("post-id") Long postId) {
        resourceService.removeImageInPost(resourceId, postId);
    }
}
