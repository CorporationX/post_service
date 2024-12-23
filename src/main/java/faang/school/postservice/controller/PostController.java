package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostViewEventPublisher postViewEventPublisher;

    @PostMapping("/posts")
    public PostDto createPostDraft(@Valid @RequestBody PostDto postDto) {
        log.info("Received a request to create a post");
        return postService.createPostDraft(postDto);
    }

    @PatchMapping("/posts/{postId}/publish")
    public PostDto publishPost(@PathVariable Long postId) {
        log.info("Received a request to publish the post with ID: {}", postId);
        return postService.publishPost(postId);
    }

    @PatchMapping("/posts/{postId}")
    public PostDto updatePost(@PathVariable Long postId,
                              @RequestBody PostDto postDto) {
        log.info("Received a request to update the post with ID: {}", postId);
        return postService.updatePost(postId, postDto);
    }

    @DeleteMapping("/posts/{postId}")
    public void softDelete(@PathVariable Long postId) {
        log.info("Received a request to delete softly the post with ID: {}", postId);
        postService.softDelete(postId);
    }

    @GetMapping("/posts/{postId}")
    public PostDto getPostById(@PathVariable Long postId, @RequestParam Long viewerId) {
        log.info("Received a request to fetch the post with ID: {}", postId);
        PostDto post = postService.getPostById(postId);
        postViewEventPublisher.publish(new PostViewEvent(
                postId,
                post.getAuthorId(),
                viewerId,
                LocalDateTime.now()
        ));
        return post;
    }

    @GetMapping("/users/{userId}/posts/drafts")
    public List<PostDto> getAllPostDraftsByUserId(@PathVariable Long userId) {
        log.info("Received a request to fetch all post drafts for the user with ID: {}", userId);
        return postService.getAllPostDraftsByUserId(userId);
    }

    @GetMapping("/projects/{projectId}/posts/drafts")
    public List<PostDto> getAllPostDraftsByProjectId(@PathVariable Long projectId) {
        log.info("Received a request to fetch all post drafts for the project with ID: {}", projectId);
        return postService.getAllPostDraftsByProjectId(projectId);
    }

    @GetMapping("/users/{userId}/posts/published")
    public List<PostDto> getAllPublishedPostsByUserId(@PathVariable Long userId) {
        log.info("Received a request to fetch all published posts for the user with ID: {}", userId);
        return postService.getAllPublishedPostsByUserId(userId);
    }

    @GetMapping("/projects/{projectId}/posts/published")
    public List<PostDto> getAllPublishedPostsByProjectId(@PathVariable Long projectId) {
        log.info("Received a request to fetch all published posts for the project with ID: {}", projectId);
        return postService.getAllPublishedPostsByProjectId(projectId);
    }
}