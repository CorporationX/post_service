package faang.school.postservice.controller;


import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/create")
    public PostDto createPost(@RequestBody CreatePostDto postDto) {
        validateCreatePost(postDto);
        return postService.createPost(postDto);
    }

    @PutMapping("/publish")
    public List<PostDto> publishPost() {
        return postService.publishPost();
    }

    @PutMapping("/update")
    public PostDto updatePost(@RequestBody UpdatePostDto postDto) {
        validateUpdatePost(postDto);
        return postService.updatePost(postDto);
    }

    @GetMapping("/delete/{postId}")
    public void softDeletePost(@PathVariable Long postId) {
        postService.softDeletePost(postId);
    }

    @GetMapping("/{postId}")
    private PostDto getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/all/author/{userId}")
    public List<PostDto> getAllPostsByAuthorId(@PathVariable Long userId) {
        return postService.getAllPostsByAuthorId(userId);
    }

    @GetMapping("/all/project/{projectId}")
    public List<PostDto> getAllPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllPostsByProjectId(projectId);
    }

    @GetMapping("/all/author/published/{userId}")
    public List<PostDto> getAllPostsByAuthorIdAndPublished(@PathVariable Long userId) {
        return postService.getAllPostsByAuthorIdAndPublished(userId);
    }

    @GetMapping("/all/project/published/{projectId}")
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
