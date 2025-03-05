package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.*;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
@Validated
public class PostController {
    private final PostService postService;

    @PostMapping("/draft")
    public PostDraftResponseDto createDraftPost(@RequestBody @Valid PostDraftCreateDto dto) {
        return postService.createDraftPost(dto);
    }

    @PutMapping("/{postId}/publish")
    public PostResponseDto publishPost(@PathVariable @Positive long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping("/{postId}/update")
    public PostResponseDto updatePost(@PathVariable @Positive long postId, @RequestBody @Valid PostUpdateDto dto) {
        return postService.updatePost(postId, dto);
    }

    @DeleteMapping("/{postId}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deletePostById(@PathVariable @Positive long postId) {
        postService.deletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostResponseDto getPost(@PathVariable @Positive long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/user/{userId}/drafts")
    public List<PostDraftResponseDto> getAllDraftPostsByUserId(@PathVariable @Positive long userId) {
        return postService.getDraftPostsByUserIdSortedCreatedAtDesc(userId);
    }

    @GetMapping("/project/{projectId}/drafts")
    public List<PostDraftResponseDto> getAllDraftPostsByProjectId(@PathVariable @Positive long projectId) {
        return postService.getDraftPostsByProjectIdSortedCreatedAtDesc(projectId);
    }

    @GetMapping("/user/{userId}/publishes")
    public List<PostResponseDto> getAllPublishPostsByUserId(@PathVariable @Positive long userId) {
        return postService.getPublishPostsByUserIdSortedCreatedAtDesc(userId);
    }

    @GetMapping("/project/{projectId}/publishes")
    public List<PostResponseDto> getAllPublishPostsByProjectId(@PathVariable @Positive long projectId) {
        return postService.getPublishPostsByProjectIdSortedCreatedAtDesc(projectId);
    }

    @GetMapping("/user/{userId}/publishes/ids")
    public List<Long> getAllPublishPostsIdsByUserId(@PathVariable @Positive long userId) {
        return postService.getPublishPostsIdsByUserIdSortedCreatedAtDesc(userId);
    }

    @GetMapping("/project/{projectId}/publishes/ids")
    public List<Long> getAllPublishPostsIdsByProjectId(@PathVariable @Positive long projectId) {
        return postService.getPublishPostsIdsByProjectIdSortedCreatedAtDesc(projectId);
    }

    @PostMapping(value = "/draft/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDraftResponseDto> createDraftPostWithFiles(
            @RequestPart("dto") @Valid PostDraftWithFilesCreateDto dto,
            @RequestPart("files") @NotNull MultipartFile[] files
    ) throws IOException {
        return ResponseEntity.ok(postService.createDraftPostWithFiles(dto, files));
    }

    @PutMapping(value = "/{postId}/update/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDto> updatePostWithFiles(
            @PathVariable("postId") @Positive long postId, @RequestPart("dto") @Valid PostUpdateDto dto,
            @RequestPart("files") @NotNull MultipartFile[] files) throws IOException {
        return ResponseEntity.ok(postService.updatePostWithFiles(postId, dto, files));
    }
}
