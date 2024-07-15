package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public void createPost(@RequestBody @Valid PostDto postDto) {
        postService.createPost(postDto);
    }

    @PutMapping
    public void updatePost(@RequestBody @Valid PostDto postDto) {
        postService.updatePost(postDto);
    }

    @PutMapping("/publish")
    public void publishPost(@RequestBody @Valid PostDto postDto) {
        postService.publishPost(postDto);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPostByPostId(@PathVariable Long postId) {
        return postService.getPostByPostId(postId);
    }

    @GetMapping("/drafts/user/{userId}")
    public List<PostDto> getAllDraftPostsByUserId(@PathVariable Long userId) {
        return postService.getAllDraftPostsByUserId(userId);
    }

    @GetMapping("/drafts/project/{projectId}")
    public List<PostDto> getAllDraftPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllDraftPostsByProjectId(projectId);
    }

    @GetMapping("/published/user/{userId}")
    public List<PostDto> getAllPublishPostsByUserId(@PathVariable Long userId) {
        return postService.getAllPublishPostsByUserId(userId);
    }

    @GetMapping("/published/project/{projectId}")
    public List<PostDto> getAllPublishPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllPublishPostsByProjectId(projectId);
    }
}
