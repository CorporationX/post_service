package faang.school.postservice.controller;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @PostMapping("/draftCreate")
    public PostDto createDraftPost(@RequestBody PostDto dto) {
        validate(dto.projectId(), dto.authorId(), dto.content());
        return postService.createDraftPost(dto);
    }

    private void validate(Long projectId, Long authorId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Post cannot be empty.");
        }
        if (projectId != null && authorId != null) {
            throw new IllegalArgumentException("Post cannot have more than 1 owner");
        }

        validateOwner(projectId, authorId);
    }

    private void validateOwner(Long projectId, Long authorId) {
        if (projectId == null && userServiceClient.getUser(authorId) == null) {
            throw new EntityNotFoundException("User with id: " + authorId + " not found.");
        }
        if (authorId == null && projectServiceClient.getProject(projectId) == null) {
            throw new EntityNotFoundException("Project with id: " + projectId + " not found");
        }
    }

    @PatchMapping("/{postId}")
    public PostDto publishPost(@PathVariable long postId) {
        return postService.publishPost(postId);
    }

    @PatchMapping("/{postId}/updatePost")
    public PostDto updatePost(@PathVariable long postId, @RequestBody PostDto dto) {
        validate(dto.projectId(), dto.authorId(), dto.content());
        return postService.updatePost(postId, dto);
    }

    @PatchMapping("/{postId}/deletePost")
    public PostDto deletePost(@PathVariable long postId) {
        return postService.deletePost(postId);
    }

    @GetMapping
    public List<PostDto> posts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable long postId) {
        return postService.getPostDtoById(postId);
    }

    @DeleteMapping("{postId}/delete")
    public void deletePostById(@PathVariable Long postId) {
        postService.deletePostById(postId);
    }

    @GetMapping("/author/{authorId}/drafts")
    public List<PostDto> getAllNotDeletedDraftsByAuthorId(@PathVariable Long authorId) {
        return postService.getAllNotDeletedDraftsByAuthorId(authorId);
    }

    @GetMapping("/project/{projectId}/drafts")
    public List<PostDto> getAllNotDeletedDraftsByProjectId(@PathVariable Long projectId) {
        return postService.getAllNotDeletedDraftsByProjectId(projectId);
    }

    @GetMapping("/author/{authorId}/posts")
    public List<PostDto> getAllNotDeletedPostsByAuthorId(@PathVariable Long authorId) {
        return postService.getAllPostsByAuthorId(authorId);
    }

    @GetMapping("project/{projectId}/posts")
    public List<PostDto> getAllNotDeletedPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllPostsByProjectId(projectId);
    }
}