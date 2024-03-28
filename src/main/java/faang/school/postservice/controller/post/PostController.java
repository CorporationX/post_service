package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Tag(name = "Posts", description = "Endpoints for managing posts")
public class PostController {
    private final PostService postService;

    @Operation(summary = "Create a post draft")
    @PostMapping
    public PostDto create(@Valid @RequestBody PostDto postDto) {
        return postService.create(postDto);
    }

    @Operation(summary = "Get post by id")
    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable @Min(1) long postId) {
        return postService.getPostById(postId);
    }

    @Operation(summary = "Update existing post")
    @PutMapping
    public PostDto update(@Valid @RequestBody PostDto postDto) {
        return postService.update(postDto);
    }

    @Operation(summary = "Publish created post draft")
    @PutMapping("/{postId}/published")
    public PostDto publish(@PathVariable @Min(1) long postId) {
        return postService.publish(postId);
    }

    @Operation(summary = "Delete a post")
    @DeleteMapping("/{postId}")
    public void delete(@PathVariable @Min(1) long postId) {
        postService.delete(postId);
    }

    @Operation(summary = "Get created post draft by user id")
    @GetMapping("/user/{userId}")
    public List<PostDto> getCreatedPostsByUserId(@PathVariable @Min(1) long userId) {
        return postService.getCreatedPostsByUserId(userId);
    }

    @Operation(summary = "Get created post draft by project id")
    @GetMapping("/project/{projectId}")
    public List<PostDto> getCreatedPostsByProjectId(@PathVariable @Min(1) long projectId) {
        return postService.getCreatedPostsByProjectId(projectId);
    }

    @Operation(summary = "Get published post by user id")
    @GetMapping("/user/{userId}/published")
    public List<PostDto> getPublishedPostsByUserId(@PathVariable @Min(1) long userId) {
        return postService.getPublishedPostsByUserId(userId);
    }

    @Operation(summary = "Get published post by project id")
    @GetMapping("/project/{projectId}/published")
    public List<PostDto> getPublishedPostsByProjectId(@PathVariable @Min(1) long projectId) {
        return postService.getPublishedPostsByProjectId(projectId);
    }
}
