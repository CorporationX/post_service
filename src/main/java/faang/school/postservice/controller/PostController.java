package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostDto createPostDraft (@RequestBody @Valid CreatePostDto createPostDto) {
        return postService.create(createPostDto);
    }

    @PostMapping("/publish/{id}")
    public PostDto publishPost (@PathVariable("id") Long postId) {
        return postService.publish(postId);
    }

    @PatchMapping("/{id}")
    public PostDto updatePost (@RequestBody @Valid UpdatePostDto updatePostDto) {
        return postService.update(updatePostDto);
    }

    @DeleteMapping("/{id}")
    public void deletePost (@PathVariable("id") Long postId) {
        postService.delete(postId);
    }

    @GetMapping("/{id}")
    public PostDto getPost (@PathVariable("id") Long postId) {
        return postService.getById(postId);
    }

    @GetMapping("/drafts/user/{id}")
    public List<PostDto> getDraftsByUser (@PathVariable("id") Long userId) {
        return postService.getDraftsByUser(userId);
    }

    @GetMapping("/drafts/project/{id}")
    public List<PostDto> getDraftsByProject (@PathVariable("id") Long projectId) {
        return postService.getDraftsByProject(projectId);
    }

    @GetMapping("/published/user/{id}")
    public List<PostDto> getPublishedByUser (@PathVariable("id") Long userId) {
        return postService.getPublishedByUser(userId);
    }

    @GetMapping("/published/project/{id}")
    public List<PostDto> getPublishedByProject (@PathVariable("id") Long projectId) {
        return postService.getPublishedByProject(projectId);
    }
}
