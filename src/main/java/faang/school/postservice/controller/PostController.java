package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public PostDto createDraft(@RequestBody PostDto postDto) {
        if (isInvalidToCreate(postDto)) {
            throw new IllegalArgumentException("Data is not enough to create a new post");
        }
        return postService.createDraft(postDto);
    }

    @PutMapping("/posts/{postId}/publish")
    public PostDto publishPost(@PathVariable Long postId) {
        validateId(postId);
        return postService.publishPost(postId);
    }

    @PutMapping("/posts/{postId}")
    public PostDto updatePost(@PathVariable Long postId, @RequestBody PostDto postDto) {
        validateId(postId);
        if (isInvalidToCreate(postDto)) {
            throw new IllegalArgumentException("Data is not enough to update the post");
        }
        return postService.updatePost(postId, postDto);
    }

    @DeleteMapping("/posts/{postId}")
    public PostDto softDelete(@PathVariable Long postId) {
        validateId(postId);
        return postService.softDelete(postId);
    }

    @GetMapping("/posts/{postId}")
    public PostDto getPostById(@PathVariable Long postId) {
        validateId(postId);
        return postService.getPostById(postId);
    }

    @GetMapping("/users/{authorId}/drafts")
    public List<PostDto> getAllDraftsByAuthorId(@PathVariable Long authorId) {
        validateId(authorId);
        return postService.getAllDraftsByAuthorId(authorId);
    }

    @GetMapping("/projects/{projectId}/drafts")
    public List<PostDto> getAllDraftsByProjectId(@PathVariable Long projectId) {
        validateId(projectId);
        return postService.getAllDraftsByProjectId(projectId);
    }

    @GetMapping("/users/{authorId}/published")
    public List<PostDto> getAllPublishedPostsByAuthorId(@PathVariable Long authorId) {
        validateId(authorId);
        return postService.getAllPublishedPostsByAuthorId(authorId);
    }

    @GetMapping("/projects/{projectId}/published")
    public List<PostDto> getAllPublishedPostsByProjectId(@PathVariable Long projectId) {
        validateId(projectId);
        return postService.getAllPublishedPostsByProjectId(projectId);
    }

    private boolean isInvalidToCreate(PostDto postDto) {
        if (postDto.getContent() == null || postDto.getContent().isBlank()) {
            return true;
        }
        return (postDto.getAuthorId() != null) == (postDto.getProjectId() != null);
    }

    private static void validateId(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Not positive id");
        }
    }
}
