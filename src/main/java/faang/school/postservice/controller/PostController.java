package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

import java.util.List;

@RestController
@RequestMapping("/posts")
@Validated
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto) {
        return ResponseEntity.ok(postService.createPost(postDto));
    }

    @PostMapping("/publish/{id}")
    public ResponseEntity<PostDto> publishPost(@NotNull @PathVariable Long id) {
        return ResponseEntity.ok(postService.publishPost(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable @NotNull Long id, @Valid @RequestBody PostDto postDto) {
        return ResponseEntity.ok(postService.updatePost(id, postDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable @NotNull Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @GetMapping("/drafts/user/{authorId}")
    public ResponseEntity<List<PostDto>> getUserDrafts(@PathVariable @NotNull Long authorId) {
        return ResponseEntity.ok(postService.getUserDrafts(authorId));
    }

    @GetMapping("/drafts/project/{projectId}")
    public ResponseEntity<List<PostDto>> getProjectDrafts(@PathVariable @NotNull Long projectId) {
        return ResponseEntity.ok(postService.getProjectDrafts(projectId));
    }

    @GetMapping("/published/user/{authorId}")
    public ResponseEntity<List<PostDto>> getUserPublishedPosts(@PathVariable @NotNull Long authorId) {
        return ResponseEntity.ok(postService.getUserPublishedPosts(authorId));
    }

    @GetMapping("/published/project/{projectId}")
    public ResponseEntity<List<PostDto>> getProjectPublishedPosts(@PathVariable @NotNull Long projectId) {
        return ResponseEntity.ok(postService.getProjectPublishedPosts(projectId));
    }

    @GetMapping("/all/hashtag/")
    public Page<PostDto> getAllPostsByHashtag(@NotNull @RequestParam String hashtagContent, Pageable pageable){
        return postService.getAllPostsByHashtagId(hashtagContent, pageable);
    }
}