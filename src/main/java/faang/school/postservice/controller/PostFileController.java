package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.DownloadFileDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.s3.PostFileService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public DownloadFileDto downloadFile(@PathVariable Long fileId) {
        return filesForPostService.downloadFile(fileId);
    }

    @DeleteMapping
    public ResourceDto deleteFile(@PathVariable Long fileId) {
        return filesForPostService.deleteFile(fileId);
    }
}
