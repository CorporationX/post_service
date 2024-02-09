package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public String createDraft(@NotNull @RequestBody PostDto postDto) {
        validatePostDto(postDto);
        Post post = postService.createDraft(postDto);
        return "Post created";
    }

    @PutMapping("/published/{postId}")
    public String publishPost(@PathVariable long postId) {
        validateId(postId);
        postService.publish(postId);
        return "Post published";
    }

    @PutMapping("/update")
    public String updatePost(@NotNull @RequestBody PostDto postDto) {
        validatePostDto(postDto);
        validateId(postDto.getId());
        postService.update(postDto);
        return "Post updated";
    }

    @DeleteMapping("/delete/{postId}")
    public String removePostSoftly(@PathVariable long postId) {
        validateId(postId);
        postService.removeSoftly(postId);
        return "Post deleted";
    }

    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable long id) {
        validateId(id);
        return postService.getPostById(id);
    }

    @GetMapping("/authors/{id}/drafts")
    public List<PostDto> getDraftsByAuthorId(@PathVariable long id) {
        validateId(id);
        return postService.getDraftsByAuthorId(id);
    }

    @GetMapping("/projects/{id}/drafts")
    public List<PostDto> getDraftsByProjectId(@PathVariable long id) {
        validateId(id);
        return postService.getDraftsByProjectId(id);
    }

    @GetMapping("/authors/{id}")
    public List<PostDto> getPublishedPostsByAuthorId(@PathVariable long id) {
        validateId(id);
        return postService.getPublishedPostsByAuthorId(id);
    }

    @GetMapping("/projects/{id}")
    public List<PostDto> getPublishedPostsByProjectId(@PathVariable long id) {
        validateId(id);
        return postService.getPublishedPostsByProjectId(id);
    }

    private void validatePostDto(PostDto postDto) {
        if (postDto.getContent() == null || postDto.getContent().isEmpty()) {
            throw new DataValidationException("A post with this content cannot be created");
        }
    }

    private void validateId(long id) {
        if (id < 1) {
            throw new DataValidationException("Invalid ID");
        }
    }
}


