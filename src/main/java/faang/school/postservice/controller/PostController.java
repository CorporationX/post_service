package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping()
    public PostDto createDraft(@Valid @RequestBody CreatePostDto postDto) {
        return postService.createDraft(postDto);
    }

    @PostMapping("/publish/{postId}")
    public PostDto publishPost(@PathVariable("postId") Long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping("/update/{postId}")
    public PostDto updatePost(@PathVariable Long postId, @Valid @RequestBody CreatePostDto postDto) {
        return postService.updatePost(postId, postDto);
    }

    @PutMapping("/soft-delete/{postId}")
    public PostDto softDeletePost(@PathVariable("postId") Long postId) {
        return postService.softDeletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable("postId") Long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/user/{userId}/draft")
    public List<PostDto> getDraftsByUser(@PathVariable("userId") Long userId) {
        return postService.getDraftsByUser(userId);
    }

    @GetMapping("/project/{projectId}/draft")
    public List<PostDto> getDraftsByProject(@PathVariable("projectId") Long projectId) {
        return postService.getDraftsByProject(projectId);
    }

    @GetMapping("/user/{userId}/published")
    public List<PostDto> getPublishedByUser(@PathVariable("userId") Long userId) {
        return postService.getPublishedByUser(userId);
    }

    @GetMapping("/project/{projectId}/published")
    public List<PostDto> getPublishedByProject(@PathVariable("projectId") Long projectId) {
        return postService.getPublishedByProject(projectId);
    }
}
