package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostResponseDto createDraftPost(@RequestBody PostRequestDto postRequestDto) {
        log.info("Starting to create draft post: {}", postRequestDto);
        PostResponseDto response = postService.createDraftPost(postRequestDto);
        log.info("Finished creating draft post: {}", postRequestDto);
        return response;
    }

    @PostMapping("/{postId}/publish")
    public PostResponseDto publishPost(@PathVariable Long postId) {
        log.info("Starting to publish post with ID: {}", postId);
        PostResponseDto response = postService.publishPost(postId);
        log.info("Finished publishing post with ID: {}", postId);
        return response;
    }

    @PutMapping("/update")
    public PostResponseDto updatePost(@RequestBody PostRequestDto postRequestDto) {
        log.info("Starting to update post: {}", postRequestDto);
        PostResponseDto response = postService.updatePost(postRequestDto);
        log.info("Finished updating post: {}", postRequestDto);
        return response;
    }

    @DeleteMapping("/{postId}")
    public PostResponseDto deletePost(@PathVariable Long postId) {
        log.info("Starting to delete post with ID: {}", postId);
        PostResponseDto response = postService.deletePost(postId);
        log.info("Finished deleting post with ID: {}", postId);
        return response;
    }

    @GetMapping("/{postId}")
    public PostResponseDto getPostById(@PathVariable Long postId) {
        log.info("Starting to fetch post by ID: {}", postId);
        PostResponseDto response = postService.getPostById(postId);
        log.info("Finished fetching post by ID: {}", postId);
        return response;
    }

    @GetMapping("/user/{userId}/drafts")
    public List<PostResponseDto> getUserDraftPosts(@PathVariable Long userId) {
        log.info("Starting to fetch draft posts for user ID: {}", userId);
        List<PostResponseDto> response = postService.getUserDraftPosts(userId);
        log.info("Finished fetching draft posts for user ID: {}", userId);
        return response;
    }

    @GetMapping("/project/{projectId}/drafts")
    public List<PostResponseDto> getProjectDraftPosts(@PathVariable Long projectId) {
        log.info("Starting to fetch draft posts for project ID: {}", projectId);
        List<PostResponseDto> response = postService.getProjectDraftPosts(projectId);
        log.info("Finished fetching draft posts for project ID: {}", projectId);
        return response;
    }

    @GetMapping("/user/{userId}/published")
    public List<PostResponseDto> getUserPublishedPosts(@PathVariable Long userId) {
        log.info("Starting to fetch published posts for user ID: {}", userId);
        List<PostResponseDto> response = postService.getUserPublishedPosts(userId);
        log.info("Finished fetching published posts for user ID: {}", userId);
        return response;
    }

    @GetMapping("/project/{projectId}/published")
    public List<PostResponseDto> getProjectPublishedPosts(@PathVariable Long projectId) {
        log.info("Starting to fetch published posts for project ID: {}", projectId);
        List<PostResponseDto> response = postService.getProjectPublishedPosts(projectId);
        log.info("Finished fetching published posts for project ID: {}", projectId);
        return response;
    }

    @PutMapping("/{postId}")
    public @Validated List<ResourceDto> addResource(@PathVariable Long postId,
                                                    @RequestParam("files") List<MultipartFile> files) {
        List<ResourceDto> resourceDtoList = postService.add(postId, files);
        return resourceDtoList;
    }

    @DeleteMapping("/{postId}/resources/{resourceId}")
    public void deleteResource(@PathVariable Long postId, @PathVariable Long resourceId) {
        postService.delete(postId, resourceId);
    }
}
