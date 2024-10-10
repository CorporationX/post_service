package faang.school.postservice.controller.post;

import faang.school.postservice.model.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;


    @GetMapping("/hashtags/{hashtag}")
    public List<PostDto> getPostsByHashtag(@PathVariable @NotBlank String hashtag) {
        return postService.getPostsByHashtag(hashtag);
    }

    @PostMapping
    public PostDto createDraftPost(@RequestBody @Validated PostDto postDto) {
        return postService.createDraftPost(postDto);
    }

    @PutMapping("/publication")
    public PostDto publishPost(@RequestBody @Validated PostDto postDto) {
        return postService.publishPost(postDto);
    }

    @PutMapping
    public PostDto updatePost(@RequestBody @Validated PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @PutMapping("/{postId}")
    public PostDto softDeletePost(@PathVariable @Positive Long postId) {
        return postService.softDeletePost(postId);
    }

    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable @Positive Long id) {
        return postService.getPost(id);
    }

    @GetMapping("/drafts/user/{userId}")
    public List<PostDto> getAllDraftsByAuthorId(@PathVariable @Positive Long userId) {
        return postService.getAllDraftsByAuthorId(userId);
    }

    @GetMapping("/drafts/project/{projectId}")
    public List<PostDto> getAllDraftsByProjectId(@PathVariable @Positive Long projectId) {
        return postService.getAllDraftsByProjectId(projectId);
    }

    @GetMapping("/user/{userId}")
    public List<PostDto> getAllPublishedPostsByAuthorId(@PathVariable @Positive Long userId) {
        return postService.getAllPublishedPostsByAuthorId(userId);
    }

    @GetMapping({"/project/{projectId}"})
    public List<PostDto> getAllPublishedPostsByProjectId(@PathVariable @Positive Long projectId) {
        return postService.getAllPublishedPostsByProjectId(projectId);
    }
}
