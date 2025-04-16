package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.MaxUploadCountExceededException;
import faang.school.postservice.service.PostServiceImpl;
import faang.school.postservice.service.PostServiceImpl;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Validated
@Slf4j
public class PostController {

    private final PostServiceImpl postService;

    @PostMapping
    public PostDto createDraft(@RequestBody PostDto postDto) {
        isInvalidToCreate(postDto);
        return postService.createDraft(postDto);
    }

    @PutMapping("/{postId}/publish")
    public PostDto publishPost(@PathVariable @Positive Long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@PathVariable @Positive Long postId, @RequestBody PostDto postDto) {
        isInvalidToCreate(postDto);
        return postService.updatePost(postId, postDto);
    }

    @DeleteMapping("/{postId}")
    public PostDto softDelete(@PathVariable @Positive Long postId) {
        return postService.softDelete(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable @Positive Long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/users/{authorId}/drafts")
    public List<PostDto> getAllDraftsByAuthorId(@PathVariable @Positive Long authorId) {
        return postService.getAllDraftsByAuthorId(authorId);
    }

    @GetMapping("/projects/{projectId}/drafts")
    public List<PostDto> getAllDraftsByProjectId(@PathVariable @Positive Long projectId) {
        return postService.getAllDraftsByProjectId(projectId);
    }

    @GetMapping("/users/{authorId}/published")
    public List<PostDto> getAllPublishedPostsByAuthorId(@PathVariable @Positive Long authorId) {
        return postService.getAllPublishedPostsByAuthorId(authorId);
    }

    @GetMapping("/projects/{projectId}/published")
    public List<PostDto> getAllPublishedPostsByProjectId(@PathVariable @Positive Long projectId) {
        return postService.getAllPublishedPostsByProjectId(projectId);
    }

    @PutMapping(value = "/{postId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImageToPost(@PathVariable @Positive Long postId,
                                                              @RequestParam("files") List<MultipartFile> files) {
        try {
            validateNumberOfFiles(files);
        } catch (MaxUploadCountExceededException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        List<ResourceDto> uploadedImages = postService.uploadImageToPost(postId, files);
        return ResponseEntity.ok(uploadedImages);
    }

    private void isInvalidToCreate(PostDto postDto) {
        if (postDto.getContent() == null || postDto.getContent().isBlank()) {
            throw new IllegalArgumentException("Data is not enough to update the post");
        }
        if ((postDto.getAuthorId() != null) == (postDto.getProjectId() != null)) {
            throw new DataValidationException("Can be only one author");
        }
    }

    private void validateNumberOfFiles(List<MultipartFile> files) {
        if (files.size() > 10) {
            log.debug("Validation hasn't been passed because of number of files");
            throw new MaxUploadCountExceededException("Too many files to upload! There must be fewer then 10");
        }
    }
}
