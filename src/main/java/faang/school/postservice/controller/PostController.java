package faang.school.postservice.controller;

import faang.school.postservice.dto.post.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostDto createPost(@RequestBody @Valid PostDto postDto) {
        return postService.createPost(postDto);
    }

    @PutMapping
    public PostDto updatePost(@RequestBody @Valid PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @PutMapping("/publish")
    public PostDto publishPost(@RequestBody @Valid PostDto postDto) {
        return postService.publishPost(postDto);
    }

    @DeleteMapping("/{postId}")
    public PostDto deletePost(@PathVariable Long postId) {
        return postService.deletePost(postId);
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

    @GetMapping("/hashtag")
    public List<PostDto> getPostsByHashtag(@RequestBody @Valid HashtagDto hashtagDto) {
        return postService.getPostsByHashtag(hashtagDto);
    }
}
