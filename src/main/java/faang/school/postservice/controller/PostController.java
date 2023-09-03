package faang.school.postservice.controller;


import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostDto createPost(@Valid @RequestBody CreatePostDto createPostDto) {
        return postService.createPost(createPostDto);
    }

    @PostMapping("/publish")
    public List<PostDto> publishPost() {
        return postService.publishPost();
    }

    @PutMapping("/{id}")
    public PostDto updatePost(@Valid Long id, @RequestBody UpdatePostDto updatePostDto) {
        return postService.updatePost(id, updatePostDto);
    }

    @DeleteMapping("/{postId}")
    public void softDeletePost(@NotNull @PathVariable Long postId) {
        postService.softDeletePost(postId);
    }

    @GetMapping("/{postId}")
    private PostDto getPostById(@NotNull @PathVariable Long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/author/{userId}/all")
    public List<PostDto> getAllPostsByAuthorId(@NotNull @PathVariable Long userId) {
        return postService.getAllPostsByAuthorId(userId);
    }

    @GetMapping("/project/{projectId}/all")
    public List<PostDto> getAllPostsByProjectId(@NotNull @PathVariable Long projectId) {
        return postService.getAllPostsByProjectId(projectId);
    }

    @GetMapping("/all/author/{userId}/published")
    public List<PostDto> getAllPostsByAuthorIdAndPublished(@NotNull @PathVariable Long userId) {
        return postService.getAllPostsByAuthorIdAndPublished(userId);
    }

    @GetMapping("/all/project/{projectId}/published")
    public List<PostDto> getAllPostsByProjectIdAndPublished(@NotNull @PathVariable Long projectId) {
        return postService.getAllPostsByProjectIdAndPublished(projectId);
    }

    @GetMapping("/all/hashtag/")
    public List<PostDto> getAllPostsByHashtag(@NotNull @RequestParam String hashtagContent){
        return postService.getAllPostsByHashtagId(hashtagContent);
    }
}
