package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDeleteDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.ImageType;
import faang.school.postservice.service.resource.ResourcePostService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ResourcePostController {
    private final ResourcePostService resourcePostService;

    @PutMapping("/posts/{postId}/resource")
    public List<ResourceDto> addResource(
            @NotNull(message = "Post cannot be empty")
            @Positive(message = "Post cannot be negative")
            @PathVariable
            Long postId,
            @NotNull
            @RequestParam(required = false, defaultValue = "HORIZONTAL")
            ImageType type,
            @NotNull(message = "File cannot be empty")
            @RequestParam("file")
            MultipartFile[] file) {
        return resourcePostService.addResources(postId, file, type);
    }

    @GetMapping("/posts/{postId}/resources")
    public List<String> getResource(
            @NotNull(message = "Post cannot be empty")
            @Positive(message = "Post cannot be negative")
            @PathVariable
            Long postId) {
        return resourcePostService.getResource(postId);
    }

    @DeleteMapping("/posts/{postId}/resources")
    public void deleteResource(
            @NotNull(message = "Post cannot be empty")
            @Positive(message = "Post cannot be negative")
            @PathVariable
            Long postId,
            @RequestBody ResourceDeleteDto deleteDto) {
        resourcePostService.deleteResource(postId, deleteDto.resourceIds());
    }
}