package faang.school.postservice.controller;


import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostDto createPost(@RequestBody CreatePostDto createPostDto) {
        validateCreatePost(createPostDto);
        return postService.createPost(createPostDto);
    }

    @PostMapping("/publish")
    public List<PostDto> publishPost() {
        return postService.publishPost();
    }

    @PutMapping
    public PostDto updatePost(@RequestBody UpdatePostDto updatePostDto) {
        validateUpdatePost(updatePostDto);
        return postService.updatePost(updatePostDto);
    }

    @DeleteMapping("/{postId}")
    public void softDeletePost(@PathVariable Long postId) {
        postService.softDeletePost(postId);
    }

    @GetMapping("/{postId}")
    private PostDto getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/author/{userId}/all")
    public List<PostDto> getAllPostsByAuthorId(@PathVariable Long userId) {
        return postService.getAllPostsByAuthorId(userId);
    }

    @GetMapping("/project/{projectId}/all")
    public List<PostDto> getAllPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllPostsByProjectId(projectId);
    }

    @GetMapping("/all/author/{userId}/published")
    public List<PostDto> getAllPostsByAuthorIdAndPublished(@PathVariable Long userId) {
        return postService.getAllPostsByAuthorIdAndPublished(userId);
    }

    @GetMapping("/all/project/{projectId}/published")
    public List<PostDto> getAllPostsByProjectIdAndPublished(@PathVariable Long projectId) {
        return postService.getAllPostsByProjectIdAndPublished(projectId);
    }

    private void validateCreatePost(CreatePostDto postDto) {
        if (postDto == null) {
            throw new DataValidationException("PostDto is null");
        }
        if (postDto.getContent() == null || postDto.getContent().isEmpty()) {
            throw new DataValidationException("Content is null");
        }
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("AuthorId and ProjectId is null");
        }
    }

    private void validateUpdatePost(UpdatePostDto postDto) {
        if (postDto == null) {
            throw new DataValidationException("PostDto is null");
        }
        if (postDto.getContent() == null || postDto.getContent().isEmpty()) {
            throw new DataValidationException("Content is null");
        }
        if (postDto.getAdId() == null || postDto.getAdId() < 1) {
            throw new DataValidationException("AdId is null");
        }
    }
}
