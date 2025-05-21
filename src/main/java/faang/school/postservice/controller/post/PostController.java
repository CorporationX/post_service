package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/drafts")
    public PostDto createPost(@Valid @RequestBody PostDto postDto) {
        log.info("Creating new post draft {} - Started", postDto);
        PostDto createdPost = postService.createPost(postDto);
        log.info("Creating new post draft {} - Finished", postDto);
        return createdPost;
    }

    @PatchMapping("/{postId}/publish")
    public PostDto publishPost(@NotNull @PathVariable("postId") Long postId) {
        log.info("Publishing post with id {} - Started", postId);
        PostDto publishedPost = postService.publishPost(postId);
        log.info("Publishing post with id {} - Finished", postId);
        return publishedPost;
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@NotNull @PathVariable("postId") Long postId, @Valid @RequestBody PostUpdateDto postUpdateDto) {
        log.info("Updating post with id {} - Started", postId);
        PostDto updatedPost = postService.updatePost(postId, postUpdateDto);
        log.info("Updating post with id {} - Finished", postId);
        return updatedPost;
    }

    @DeleteMapping("/{postId}")
    public PostDto deletePost(@NotNull @PathVariable("postId") Long postId) {
        log.info("Deleting post with id {} - Started", postId);
        PostDto deletedPost = postService.deletePost(postId);
        log.info("Deleting post with id {} - Finished", postId);
        return deletedPost;
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@NotNull @PathVariable("postId") Long postId) {
        log.info("Getting post with id {} - Started", postId);
        PostDto foundPost = postService.getPostById(postId);
        log.info("getting post with id {} - Finished", postId);
        return foundPost;
    }

    @GetMapping("/drafts/users/{userId}")
    public List<PostDto> getNotDeletedUserDrafts(@NotNull @PathVariable("userId") Long userId) {
        log.info("Getting drafted posts for user with id {} - Started", userId);
        List<PostDto> userPosts = postService.getNotDeletedUserDrafts(userId);
        log.info("Getting drafted posts for user with id {} - Finished", userId);
        return userPosts;
    }

    @GetMapping("/drafts/projects/{projectId}")
    public List<PostDto> getNotDeletedProjectDrafts(@NotNull @PathVariable("projectId") Long projectId) {
        log.info("Getting drafted posts for project with id {} - Started", projectId);
        List<PostDto> projectPosts = postService.getNotDeletedProjectDrafts(projectId);
        log.info("Getting drafted posts for project with id {} - Finished", projectId);
        return projectPosts;
    }

    @GetMapping("/published/users/{userId}")
    public List<PostDto> getNotDeletedUserPublished(@NotNull @PathVariable("userId") Long userId) {
        log.info("Getting published posts for user with id {} - Started", userId);
        List<PostDto> userPosts = postService.getNotDeletedUserPublished(userId);
        log.info("Getting published posts for user with id {} - Finished", userId);
        return userPosts;
    }

    @GetMapping("/published/projects/{projectId}")
    public List<PostDto> getNotDeletedProjectPublished(@NotNull @PathVariable("projectId") Long projectId) {
        log.info("Getting published posts for project with id {} - Started", projectId);
        List<PostDto> projectPosts = postService.getNotDeletedProjectPublished(projectId);
        log.info("Getting published posts for project with id {} - Finished", projectId);
        return projectPosts;
    }
}