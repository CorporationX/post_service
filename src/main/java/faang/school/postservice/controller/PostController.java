package faang.school.postservice.controller;

import faang.school.postservice.dto.posts.PostCreatingRequest;
import faang.school.postservice.dto.posts.PostResultResponse;
import faang.school.postservice.dto.posts.PostUpdatingDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Validated
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostResultResponse createPost(@RequestBody PostCreatingRequest postCreatingDto) {
        return postService.createPost(postCreatingDto);
    }

    @PatchMapping("/{postId}/publish")
    public PostResultResponse publishPost(@PathVariable @NotNull Long postId) {
        return postService.publishPost(postId);
    }

    @PatchMapping("/{postId}/update")
    public PostResultResponse updatePost(@RequestBody PostUpdatingDto postUpdatingDto) {
        return postService.updatePost(postUpdatingDto);
    }

    @DeleteMapping("/{postId}")
    public PostResultResponse softDelete(@PathVariable @NotNull Long postId) {
        return postService.softDelete(postId);
    }

    @GetMapping("/author/{authorId}/no-published")
    public List<PostResultResponse> getNoPublishedPostsByAuthor(@PathVariable @NotNull Long authorId) {
        return postService.getNoPublishedPostsByAuthor(authorId);
    }

    @GetMapping("/project/{projectId}/no-published")
    public List<PostResultResponse> getNoPublishedPostsByProject(@PathVariable @NotNull Long projectId) {
        return postService.getNoPublishedPostsByProject(projectId);
    }

    @GetMapping("/author/{authorId}/published")
    public List<PostResultResponse> getPublishedPostsByAuthor(@PathVariable @NotNull Long authorId) {
        return postService.getPublishedPostsByAuthor(authorId);
    }

    @GetMapping("/project/{projectId}/published")
    public List<PostResultResponse> getPublishedPostsByProject(@PathVariable @NotNull Long projectId) {
        return postService.getPublishedPostsByProject(projectId);
    }
}
