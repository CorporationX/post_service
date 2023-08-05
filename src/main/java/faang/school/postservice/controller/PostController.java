package faang.school.postservice.controller;


import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostDto createPost(CreatePostDto postDto) {
        validateCreatePost(postDto);
        return postService.createPost(postDto);
    }


    public List<PostDto> publishPost() {
        return postService.publishPost();
    }

    public PostDto updatePost(UpdatePostDto postDto) {
        validateUpdatePost(postDto);
        return postService.updatePost(postDto);
    }

    public void softDeletePost(Long postId) {
        postService.softDeletePost(postId);
    }

    private PostDto getPostById(Long id) {
        return postService.getPostById(id);
    }

    public List<PostDto> getAllPostsByAuthorId(Long userId) {
        return postService.getAllPostsByAuthorId(userId);
    }

    public List<PostDto> getAllPostsByProjectId(Long projectId) {
        return postService.getAllPostsByProjectId(projectId);
    }

    public List<PostDto> getAllPostsByAuthorIdAndPublished(Long userId) {
        return postService.getAllPostsByAuthorIdAndPublished(userId);
    }

    public List<PostDto> getAllPostsByProjectIdAndPublished(Long projectId) {
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
