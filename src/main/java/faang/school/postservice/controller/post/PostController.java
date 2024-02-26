package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
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
    public String createDraft(@Validated @RequestBody PostDto postDto) {
        postService.createDraft(postDto);
        return "Post created";
    }

    @PutMapping("/published/{postId}")
    public String publishPost(@PathVariable @Positive(message = "Id must be greater than zero") long postId) {
        postService.publish(postId);
        return "Post published";
    }

    @PutMapping("/update")
    public String updatePost(@Validated @RequestBody PostDto postDto) {
        postService.update(postDto);
        return "Post updated";
    }

    @DeleteMapping("/delete/{postId}")
    public String removePostSoftly(@PathVariable @Positive(message = "Id must be greater than zero") long postId) {
        postService.removeSoftly(postId);
        return "Post deleted";
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


