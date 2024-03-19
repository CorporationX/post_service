package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostDto create(@RequestBody @Valid PostDto postDto) {
        return postService.create(postDto);
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable @Min(1) long postId) {
        return postService.getPostById(postId);
    }

    @PutMapping
    public PostDto update(@RequestBody @Valid PostDto postDto) {
        return postService.update(postDto);
    }

    @PutMapping("/{postId}/published")
    public PostDto publish(@PathVariable @Min(1) long postId) {
        return postService.publish(postId);
    }

    @DeleteMapping("/{postId}")
    public void delete(@PathVariable @Min(1) long postId) {
        postService.delete(postId);
    }

    @GetMapping("/user/{userId}")
    public List<PostDto> getCreatedPostsByUserId(@PathVariable @Min(1) long userId) {
        return postService.getCreatedPostsByUserId(userId);
    }

    @GetMapping("/project/{projectId}")
    public List<PostDto> getCreatedPostsByProjectId(@PathVariable @Min(1) long projectId) {
        return postService.getCreatedPostsByProjectId(projectId);
    }

    @GetMapping("/user/{userId}/published")
    public List<PostDto> getPublishedPostsByUserId(@PathVariable @Min(1) long userId) {
        return postService.getPublishedPostsByUserId(userId);
    }

    @GetMapping("/project/{projectId}/published")
    public List<PostDto> getPublishedPostsByProjectId(@PathVariable @Min(1) long projectId) {
        return postService.getPublishedPostsByProjectId(projectId);
    }
}
