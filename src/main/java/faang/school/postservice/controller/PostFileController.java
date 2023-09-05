package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.DownloadFileDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.dto.resource.ResourceDtoInputStream;
import faang.school.postservice.service.s3.PostFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post/resource")
public class PostFileController {
    private final PostFileService filesForPostService;

    @PutMapping("/{postId}")
    public List<ResourceDto> addFiles(@PathVariable Long postId, @RequestBody List<MultipartFile> multipartFile) {
        return filesForPostService.addFiles(postId, multipartFile);
    }

    @GetMapping("/InputStream/{fileId}")
    public ResponseEntity<InputStreamResource> downloadFileInputStream(@PathVariable Long fileId) {
        ResourceDtoInputStream resourceDtoInputStream = filesForPostService.downloadFileInputStream(fileId);
        InputStreamResource inputStreamResource = new InputStreamResource(resourceDtoInputStream.getInputStream());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resourceDtoInputStream.getResourceDto().getName() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resourceDtoInputStream.getResourceDto().getSize())
                .contentType(MediaType.parseMediaType(resourceDtoInputStream.getResourceDto().getType()))
                .body(inputStreamResource);
    }

    @GetMapping("/{fileId}")
    public DownloadFileDto downloadFile(@PathVariable Long fileId) {
        return filesForPostService.downloadFile(fileId);
    }

    @DeleteMapping("/{fileId}")
    public ResourceDto deleteFile(@PathVariable Long fileId) {
        return filesForPostService.deleteFile(fileId);
    }
}
