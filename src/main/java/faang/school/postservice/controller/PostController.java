package faang.school.postservice.controller;

import faang.school.postservice.docs.post.CreatePostDoc;
import faang.school.postservice.docs.post.DeletePostDoc;
import faang.school.postservice.docs.post.GetPostByHashtagDoc;
import faang.school.postservice.docs.post.GetDraftPostByAuthorDoc;
import faang.school.postservice.docs.post.GetDraftPostByProjectDoc;
import faang.school.postservice.docs.post.GetPostDoc;
import faang.school.postservice.docs.post.GetPublishedPostByAuthorDoc;
import faang.school.postservice.docs.post.GetPublishedPostByProjectDoc;
import faang.school.postservice.docs.post.PublishPostDoc;
import faang.school.postservice.docs.post.UpdatePostDoc;
import faang.school.postservice.docs.post.UpdatePostResourcesDoc;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Post API", description = "This controller handles post operations")
public class PostController {
    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CreatePostDoc
    public ResponseEntity<ResponsePostDto> create(
            @RequestPart("createPostDto") @Valid CreatePostDto createPostDto,
            @RequestPart(value = "file", required = false) @Size(max = 10) List<MultipartFile> files
    ) {
        files.forEach(file -> log.info("Image File uploaded: {}", file.getOriginalFilename()));
        log.info("Request for new post from user: {}", createPostDto.getAuthorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(createPostDto, files));
    }

    @PutMapping("{postId}/resources")
    @UpdatePostResourcesDoc
    public ResponseEntity<ResponsePostDto> updateResources(@PathVariable
                                                           @NotNull(message = "Post id is required")
                                                           @Positive(message = "Post id must be positive number")
                                                           Long postId,
                                                           @RequestPart(value = "files", required = false)
                                                           @Size(max = 10, message = "Post can have maximum 10 files")
                                                           List<MultipartFile> files,
                                                           @RequestParam(value = "resourceDeleteKeys", required = false)
                                                           List<String> resourceDeleteKeys
    ) {
        log.info("Updating resources in post with id '{}'", postId);
        return ResponseEntity.ok(postService.updatePostResources(postId, files, resourceDeleteKeys));
    }

    @PutMapping("{postId}/publish")
    @PublishPostDoc
    public ResponseEntity<ResponsePostDto> publish(
            @PathVariable
            @Positive(message = "Post id must be positive")
            @NotNull(message = "Post id cannot be null")
            Long postId
    ) {
        log.info("Request for publish post: {}", postId);
        return ResponseEntity.ok(postService.publish(postId));
    }

    @PutMapping("/{postId}")
    @UpdatePostDoc
    public ResponseEntity<ResponsePostDto> update(
            @PathVariable
            @NotNull(message = "Post id cannot be null")
            @Positive(message = "Post id must be positive")
            Long postId,
            @Valid @RequestBody UpdatePostDto updatePostDto
    ) {
        log.info("Request for update post: {} with content: {}", postId, updatePostDto.getContent());
        return ResponseEntity.ok(postService.update(postId, updatePostDto));
    }

    @DeleteMapping("/{postId}")
    @DeletePostDoc
    public ResponseEntity<Void> delete(
            @PathVariable
            @NotNull(message = "Post id cannot be null")
            @Positive(message = "Post id must be positive") Long postId
    ) {
        log.info("Request for delete post: {}", postId);
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    @GetPostDoc
    public ResponseEntity<ResponsePostDto> getPostById(
            @PathVariable
            @NotNull(message = "Post id cannot be null")
            @Positive(message = "Post id must be positive") Long postId
    ) {
        log.info("Request for get post: {}", postId);
        return ResponseEntity.ok(postService.getById(postId));
    }

    @GetMapping("/draft/author/{userId}")
    @GetDraftPostByAuthorDoc
    public ResponseEntity<List<ResponsePostDto>> getDraftByUserId(
            @PathVariable
            @NotNull(message = "User id cannot be null")
            @Positive(message = "User id must be positive") Long userId
    ) {
        log.info("Request for get draft posts for user: {}", userId);
        return ResponseEntity.ok(postService.getDraftsByUserId(userId));
    }

    @GetMapping("/draft/project/{projectId}")
    @GetDraftPostByProjectDoc
    public ResponseEntity<List<ResponsePostDto>> getDraftByProjectId(
            @PathVariable
            @NotNull(message = "Post id cannot be null")
            @Positive(message = "Post id must be positive") Long projectId
    ) {
        log.info("Request for get draft posts for project: {}", projectId);
        return ResponseEntity.ok(postService.getDraftsByProjectId(projectId));
    }

    @GetMapping("/published/author/{userId}")
    @GetPublishedPostByAuthorDoc
    public ResponseEntity<List<ResponsePostDto>> getPublishedByUserId(
            @PathVariable
            @NotNull(message = "User id cannot be null")
            @Positive(message = "User id must be positive")
            Long userId
    ) {
        log.info("Request for get published posts for user: {}", userId);
        return ResponseEntity.ok(postService.getPublishedByUserId(userId));
    }

    @GetMapping("/published/project/{projectId}")
    @GetPublishedPostByProjectDoc
    public ResponseEntity<List<ResponsePostDto>> getPublishedByProjectId(
            @PathVariable
            @NotNull(message = "Project id cannot be null")
            @Positive(message = "Project id must be positive")
            Long projectId,
            @RequestParam
            @NotNull(message = "Page number cannot be null")
            @NotBlank(message = "Page number cannot be blank")
            Long authorId
    ) {
        log.info("Request for get published posts for project: {}", projectId);
        return ResponseEntity.ok(postService.getPublishedByProjectId(projectId, authorId));
    }

    @GetMapping("/hashtag/{tag}")
    @GetPostByHashtagDoc
    public ResponseEntity<List<ResponsePostDto>> searchHashtag(
            @PathVariable
            @NotBlank(message = "Tag cannot be empty")
            String tag
    ) {
        log.info("Request for get posts by tag: {}", tag);
        return ResponseEntity.ok(postService.findByHashtags(tag));
    }
}
