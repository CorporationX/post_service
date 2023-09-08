package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@Tag(name="Post Controller")
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    @Operation(summary = "Create a post draft")
    public PostDto crateDraftPost(@RequestBody @Validated PostDto postDto,
                                  @RequestParam(value = "files", required = false) MultipartFile[] files) {
        return postService.createDraftPost(postDto, files);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Publish a post with ID")
    public PostDto publishPost(@PathVariable Long id) {
        return postService.publishPost(id);
    }

    @PutMapping
    @Operation(summary = "Update a post")
    public PostDto updatePost(@RequestBody @Validated PostDto postDto,
                              @RequestParam(value = "deletedFileIds", required = false) List<Long> deletedFileIds,
                              @RequestParam(value = "addedFiles", required = false) MultipartFile[] addedFiles) {
        return postService.updatePost(postDto, deletedFileIds, addedFiles);
    }

    @DeleteMapping("/{id}")
    public PostDto softDeletePost(@PathVariable Long id) {
        return postService.softDeletePost(id);
    }

    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/drafts/users/{id}")
    public List<PostDto> getDraftPostsByUser(@PathVariable Long id) {
        return postService.getDraftPostsByUserId(id);
    }

    @GetMapping("/drafts/projects/{id}")
    public List<PostDto> getDraftPostsByProject(@PathVariable Long id) {
        return postService.getDraftPostsByProjectId(id);
    }

    @GetMapping("/users/{id}")
    public List<PostDto> getPostsByUser(@PathVariable Long id) {
        return postService.getPostsByUserId(id);
    }

    @GetMapping("/projects/{id}")
    public List<PostDto> getPostsByProject(@PathVariable Long id) {
        return postService.getPostsByProjectId(id);
    }
}
