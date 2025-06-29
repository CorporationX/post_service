package faang.school.postservice.controller;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
    public PostResponseDto createDraftPost(@RequestBody PostRequestDto dto) {
        validate(dto.projectId(), dto.authorId());
        return postService.createDraftPost(dto);
    }

    private void validate(Long projectId, Long authorId) {
        if (projectId != null && authorId != null) {
            throw new IllegalArgumentException("Post cannot have more than 1 owner");
        }
        if (projectId == null && userServiceClient.getUser(authorId) == null) {
            throw new EntityNotFoundException("User with id: " + authorId + " not found.");
        }
        if (authorId == null && projectServiceClient.getProject(projectId) == null) {
            throw new EntityNotFoundException("Project with id: " + projectId + " not found");
        }
    }

    @PatchMapping("/{postId}")
    public PostResponseDto publishPost(@PathVariable long postId) {
        return postService.publishPost(postId);
    }

    @PatchMapping("/{postId}/updatePost")
    public PostResponseDto updatePost(@PathVariable long postId, @RequestBody PostRequestDto dto) {
        validate(dto.projectId(), dto.authorId());
        return postService.updatePost(postId, dto);
    }

    @PatchMapping("/{postId}/deletePost")
    public PostResponseDto deletePost(@PathVariable long postId) {
        return postService.deletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostResponseDto getPostById(@PathVariable long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/author/{authorId}/drafts")
    public List<PostResponseDto> getAllNotDeletedDraftsByAuthorId(@PathVariable Long authorId) {
        return postService.getAllNotDeletedDraftsByAuthorId(authorId);
    }

    @GetMapping("/project/{projectId}/drafts")
    public List<PostResponseDto> getAllNotDeletedDraftsByProjectId(@PathVariable Long projectId) {
        return postService.getAllNotDeletedDraftsByProjectId(projectId);
    }

    @GetMapping("/author/{authorId}/posts")
    public List<PostResponseDto> getAllNotDeletedPostsByAuthorId(@PathVariable Long authorId) {
        return postService.getAllPostsByAuthorId(authorId);
    }

    @GetMapping("project/{projectId}/posts")
    public List<PostResponseDto> getAllNotDeletedPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllPostsByProjectId(projectId);
    }
}