package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @PostMapping("/draft")
    public PostResponseDto createDraftPost(@RequestBody PostRequestDto postRequestDto) {
        log.info("Creating draft post: {}", postRequestDto);
        return postService.createDraftPost(postRequestDto);
    }

    @PostMapping("/publish")
    public PostResponseDto publishPost(@RequestBody PostRequestDto postRequestDto) {
        log.info("Publishing post: {}", postRequestDto);
        return postService.publishPost(postRequestDto);
    }

    @PutMapping("/update")
    public PostResponseDto updatePost(@RequestBody PostRequestDto postRequestDto) {
        log.info("Updating post: {}", postRequestDto);
        return postService.updatePost(postRequestDto);
    }

    @DeleteMapping("/delete")
    public PostResponseDto deletePost(@RequestBody PostRequestDto postRequestDto) {
        log.info("Deleting post: {}", postRequestDto);
        return postService.deletePost(postRequestDto);
    }

    @GetMapping("/get/{postId}")
    public PostResponseDto getPostById(@PathVariable Long postId) {
        log.info("Fetching post by ID: {}", postId);
        return postService.getPostById(postId);
    }

    @GetMapping("/drafts/user/{userId}")
    public List<PostResponseDto> getUserDraftPosts(@PathVariable Long userId) {
        log.info("Fetching draft posts for user ID: {}", userId);
        return postService.getUserDraftPosts(userId);
    }

    @GetMapping("/drafts/project/{projectId}")
    public List<PostResponseDto> getProjectDraftPosts(@PathVariable Long projectId) {
        log.info("Fetching draft posts for project ID: {}", projectId);
        return postService.getProjectDraftPosts(projectId);
    }

    @GetMapping("/published/user/{userId}")
    public List<PostResponseDto> getUserPublishedPosts(@PathVariable Long userId) {
        log.info("Fetching published posts for user ID: {}", userId);
        return postService.getUserPublishedPosts(userId);
    }

    @GetMapping("/published/project/{projectId}")
    public List<PostResponseDto> getProjectPublishedPosts(@PathVariable Long projectId) {
        log.info("Fetching published posts for project ID: {}", projectId);
        return postService.getProjectPublishedPosts(projectId);
    }
}