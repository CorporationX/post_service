package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.PostFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FilesForPostController {
    private final PostFileService filesForPostService;

    @PutMapping
    public ResourceDto AddingFilesToAPost(@PathVariable Long postId, @RequestBody List<MultipartFile> multipartFile) {
        return filesForPostService.AddingFilesToAPost(postId, multipartFile);
    }

    @DeleteMapping
    public void deleteFile(@PathVariable Long fileId) {
        filesForPostService.deleteFile(fileId);
    }

}
