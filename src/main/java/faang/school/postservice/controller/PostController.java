package faang.school.postservice.controller;


import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @PostMapping("/draft")
    public ResponseEntity<PostDto> createDraft( @RequestBody PostDto postDto) {
        log.info("Received request to create draft post: {}", postDto);
        PostDto createdDraft = postService.createDraft(postDto);
        //log.info("Draft post created successfully with ID: {}", createdDraft.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDraft);
    }


    @GetMapping("/{postId}")
    public PostDto getPost(@PathVariable long postId) {
        log.info("Received request to get post with ID = {}", postId);
        return postService.getPost(postId);
    }

    @PutMapping("/{postId}/publish")
    public PostDto publishPost(@PathVariable long postId) {
        log.info("Received request to publish draft with ID = {}", postId);
        return postService.publishPost(postId);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@PathVariable long postId, @RequestBody String updatedContent) {
        log.info("Received request to update post with ID = {}", postId);
        PostDto updatedPost = postService.updatePost(postId, updatedContent);
        log.info("Post with ID = {} is updated successfully", postId);
        return updatedPost;
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> softDeletePost(@PathVariable long postId) {
        log.info("Received request to delete post with ID = {}", postId);
        postService.softDeletePost(postId);
        log.info("post with ID = {} was deleted successfully", postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/draft/by-author/{authorId}")
    public List<PostDto> getAllDraftsByAuthorId(@PathVariable long authorId) {
        log.info("Received request to get all drafts by author with ID = {}", authorId);
        return postService.getAllDraftsByAuthorId(authorId);
    }

    @GetMapping("/draft/by-project/{projectId}")
    public List<PostDto> getAllDraftsByProjectId(@PathVariable long projectId) {
        log.info("Received request to get all drafts by project with ID = {}", projectId);
        return postService.getAllDraftsByProjectId(projectId);
    }

    @GetMapping("/by-author/{authorId}")
    public List<PostDto> getAllPostsByAuthorId(@PathVariable long authorId) {
        log.info("Received request to get all posts by author with ID = {}", authorId);
        return postService.getAllPostsByAuthorId(authorId);
    }

    @GetMapping("/by-project/{projectId}")
    public List<PostDto> getAllPostsByProjectId(@PathVariable long projectId) {
        log.info("Received request to get all posts by project with ID = {}", projectId);
        return postService.getAllPostsByProjectId(projectId);
    }
}
