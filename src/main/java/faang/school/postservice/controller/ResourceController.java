package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.ResourceService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
@Validated
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping("/{resourceId}/file")
    public ResponseEntity<byte[]> getResource(@PathVariable @Min(1) long resourceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(resourceService.getResourceById(resourceId).getType()));
        byte[] file = resourceService.downloadResource(resourceId);
        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }

    @PutMapping("/{postId}/add")
    public List<ResourceDto> addResources(@PathVariable @Min(1) long postId,
                                         @RequestParam List<MultipartFile> files) {
        return resourceService.addResources(postId, files);
    }

    @DeleteMapping("/{resourceIds}")
    public List<ResourceDto> deleteResource(@PathVariable List<Long> resourceIds) {
        return resourceService.deleteResources(resourceIds);
    }
}
