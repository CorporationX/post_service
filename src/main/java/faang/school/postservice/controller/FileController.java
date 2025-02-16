package faang.school.postservice.controller;

import faang.school.postservice.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "File Management", description = "API for Post File Management")
@RequiredArgsConstructor
@RequestMapping("/post/files")
@RestController
public class FileController {
    private final FileService fileService;

    @Value("${file-controller.upload.max-files-per-post}")
    private int maxFiles;

    @Operation(summary = "Upload files to a post",
            description = "Uploads a list of files to a specified post")
    @PostMapping
    public void addFiles(
            @Parameter(description = "List of files to upload", required = true)
            @RequestParam List<MultipartFile> files,
            @Parameter(description = "ID of the post to upload files to", required = true)
            @RequestParam("postId") Long postId) {
        if (files.size() > maxFiles) {
            throw new IllegalArgumentException("Cannot upload more than 10 files per post");
        }
        fileService.uploadFiles(postId, files);
    }

    @Operation(summary = "Delete files from posts",
            description = "Deletes a list of files from the posts they are associated with")
    @DeleteMapping
    public void deleteFiles(
            @Parameter(description = "List of file IDs to delete", required = true)
            @RequestBody List<String> fileIds) {
        fileService.deleteFiles(fileIds);
    }

    @Operation(summary = "Get presigned URL for a file",
            description = "Generates a presigned URL for accessing a file")
    @GetMapping("/presigned-url")
    public ResponseEntity<String> getPresignedUrl(
            @Parameter(description = "ID of the file to get the presigned URL for", required = true)
            @RequestParam("fileId") String fileId) {
        String presignedUrl = fileService.getPresignedUrl(fileId);
        return ResponseEntity.ok(presignedUrl);
    }

    @Operation(summary = "Get file bytes", description = "Retrieves the bytes of a file")
    @GetMapping
    public ResponseEntity<byte[]> getObjectBytes(
            @Parameter(description = "ID of the file to retrieve bytes for", required = true)
            @RequestParam("fileId") String fileId) {
        byte[] object = fileService.getObjectBytes(fileId);
        return ResponseEntity.ok(object);
    }
}
