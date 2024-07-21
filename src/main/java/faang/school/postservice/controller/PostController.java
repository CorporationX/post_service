package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Validated
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public PostDto createPost(@Valid @RequestBody PostDto postDto) {
        return postService.createPost(postDto);
    }

    @PostMapping("/published")
    public PostDto publishPost(@Valid @RequestBody PostDto postDto) {
        return postService.publishPost(postDto.getId());
    }

    @PutMapping
    public PostDto updatePost(@Valid @RequestBody PostDto postDto) {
        return postService.updatePost(postDto.getId(), postDto.getContent(), postDto.getPublishedAt());
    }

    @DeleteMapping("/{postId}")
    public void deletePostById(@PathVariable Long postId) {
        postService.deletePostById(postId);
    }

    @GetMapping("/{postId}")
    public PostDto findById(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/draft/user/{userId}")
    public List<PostDto> getAllPostsDraftsByUserAuthorId(@PathVariable Long userId) {
        return postService.getAllPostsDraftsByUserAuthorId(userId);
    }

    @GetMapping("/draft/project/{projectId}")
    public List<PostDto> getAllPostsDraftsByProjectAuthorId(@PathVariable Long projectId) {
        return postService.getAllPostsDraftsByProjectAuthorId(projectId);
    }

    @GetMapping("/published/user/{userId}")
    public List<PostDto> getAllPublishedNotDeletedPostsByUserAuthorId(@PathVariable Long userId) {
        return postService.getAllPublishedNotDeletedPostsByUserAuthorId(userId);
    }

    @GetMapping("/published/project/{projectId}")
    public List<PostDto> getAllPublishedNotDeletedPostsByProjectAuthorId(@PathVariable Long projectId) {
        return postService.getAllPublishedNotDeletedPostsByProjectAuthorId(projectId);
    }
}
