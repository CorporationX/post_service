package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.PostValidator;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostValidator postValidator;

    @PostMapping("/draft")
    @Operation(description = "Create new draft post")
    public PostDto createDraftPost(@RequestBody @Valid PostDto postDto) {
        postValidator.validateAuthorCount(postDto);
        return postService.createDraftPost(postDto);
    }

    @PutMapping("/drafts/{id}")
    @Operation(description = "Publish draft post")
    public PostDto publishDraftPost(@PathVariable Long id) {
        return postService.publishDraftPost(id);
    }

    @PutMapping("/{id}")
    @Operation(description = "Update post")
    public PostDto updatePost(@RequestBody @Valid PostDto postDto, @PathVariable Long id) {
        return postService.updatePost(postDto, id);
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Delete post")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @GetMapping("/{id}")
    @Operation(description = "Get post")
    public PostDto getPost(@PathVariable long id) {
        return postService.getPost(id);
    }

    @GetMapping("/drafts/user/{userId}")
    @Operation(description = "Get User's drafts")
    public List<PostDto> getUserDrafts(@PathVariable Long userId) {
        return postService.getUserDrafts(userId);
    }

    @GetMapping("/drafts/project/{projectId}")
    @Operation(description = "Get project's drafts")
    public List<PostDto> getProjectDrafts(@PathVariable Long projectId) {
        return postService.getProjectDrafts(projectId);
    }

    @GetMapping("/user/{userId}")
    @Operation(description = "Get user's posts")
    public List<PostDto> getUserPosts(@PathVariable Long userId) {
        return postService.getUserPosts(userId);
    }

    @GetMapping("/project/{projectId}")
    @Operation(description = "Get project's posts")
    public List<PostDto> getProjectPosts(@PathVariable Long projectId) {
        return postService.getProjectPosts(projectId);
    }
}

