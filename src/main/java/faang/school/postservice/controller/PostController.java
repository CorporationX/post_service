package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public PostDto createDraft(@Validated @RequestBody PostDto postDto) {
        return postService.createDraft(postDto);
    }

    @PutMapping("/published/{postId}")
    public PostDto publishPost(@PathVariable @Positive(message = "Id must be greater than zero") long postId) {
        return postService.publish(postId);
    }

    @PutMapping("/update")
    public PostDto updatePost(@Validated @RequestBody PostDto postDto) {
        return postService.update(postDto);
    }

    @DeleteMapping("/delete/{postId}")
    public PostDto removePostSoftly(@PathVariable @Positive(message = "Id must be greater than zero") long postId) {
        return postService.removeSoftly(postId);
    }

    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/authors/{id}/drafts")
    public List<PostDto> getDraftsByAuthorId(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getDraftsByAuthorId(id);
    }

    @GetMapping("/projects/{id}/drafts")
    public List<PostDto> getDraftsByProjectId(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getDraftsByProjectId(id);
    }

    @GetMapping("/authors/{id}")
    public List<PostDto> getPublishedPostsByAuthorId(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getPublishedPostsByAuthorId(id);
    }

    @GetMapping("/projects/{id}")
    public List<PostDto> getPublishedPostsByProjectId(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getPublishedPostsByProjectId(id);
    }
}
