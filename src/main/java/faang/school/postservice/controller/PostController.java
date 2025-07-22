package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Post", description = "Operations related to posts")
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "Create new post in draft state",
            description = "Creates a new post and leaves it unpublished"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PostDto createPostDraft (@RequestBody @Valid CreatePostDto createPostDto) {
        return postService.create(createPostDto);
    }

    @Operation(
            summary = "Publish post by ID",
            description = "Publishes the post if it is not already published or deleted."
    )
    @PostMapping("/publish/{id}")
    public PostDto publishPost (@PathVariable("id") Long postId) {
        return postService.publish(postId);
    }

    @Operation(
            summary = "Update a post by ID",
            description = "Updates a post according to provided payload"
    )
    @PatchMapping("/{id}")
    public PostDto updatePost (@RequestBody @Valid UpdatePostDto updatePostDto) {
        return postService.update(updatePostDto);
    }

    @Operation(summary = "Delete a post by ID")
    @DeleteMapping("/{id}")
    public void deletePost (@PathVariable("id") Long postId) {
        postService.delete(postId);
    }

    @Operation(summary = "Get an existing post by ID")
    @GetMapping("/{id}")
    public PostDto getPost (@PathVariable("id") Long postId) {
        return postService.getById(postId);
    }

    @Operation(summary = "Get unpublished posts authored by a given user ID")
    @GetMapping("/drafts/user/{id}")
    public List<PostDto> getDraftsByUser (@PathVariable("id") Long userId) {
        return postService.getDraftsByUser(userId);
    }

    @Operation(summary = "Get unpublished posts authored by a given project ID")
    @GetMapping("/drafts/project/{id}")
    public List<PostDto> getDraftsByProject (@PathVariable("id") Long projectId) {
        return postService.getDraftsByProject(projectId);
    }

    @Operation(summary = "Get published posts authored by a given user ID")
    @GetMapping("/published/user/{id}")
    public List<PostDto> getPublishedByUser (@PathVariable("id") Long userId) {
        return postService.getPublishedByUser(userId);
    }

    @Operation(summary = "Get published posts authored by a given project ID")
    @GetMapping("/published/project/{id}")
    public List<PostDto> getPublishedByProject (@PathVariable("id") Long projectId) {
        return postService.getPublishedByProject(projectId);
    }
}
