package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final PostValidator postValidator;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        postValidator.validateBlankContent(postDto);
        return ResponseEntity.ok(postService.createPostDraft(postDto));
    }

    @GetMapping("/{postId}/publish")
    @ResponseStatus(HttpStatus.OK)
    public void publishPost(@PathVariable long postId) {
        postService.publishPost(postId);
    }

    @PostMapping
    public PostDto updatePost(@RequestBody PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @GetMapping("/{postId}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void softDeletePost(@PathVariable long postId) {
        postService.softDeletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPost(@PathVariable long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/find/notDeletedNotPublishedByUserId/{userId}")
    public List<PostDto> getPostsNotDeletedNotPublishedByUserId(@PathVariable long userId) {
        return postService.getPostsNotDeletedNotPublishedByUserId(userId);
    }

    @GetMapping("/find/notDeletedNotPublishedByProjectId/{projectId}")
    public List<PostDto> getPostsNotDeletedNotPublishedByProjectId(@PathVariable long projectId) {
        return postService.getPostsNotDeletedNotPublishedByProjectId(projectId);
    }

    @GetMapping("/find/publishedNotDeletedByUserId/{userId}")
    public List<PostDto> getPostsPublishedNotDeletedByUserId(@PathVariable long userId) {
        return postService.getPostsPublishedNotDeletedByUserId(userId);
    }

    @GetMapping("/find/publishedNotDeletedByProjectId/{projectId}")
    public List<PostDto> getPostsPublishedNotDeletedByProjectId(@PathVariable long projectId) {
        return postService.getPostsPublishedNotDeletedByProjectId(projectId);
    }
}
