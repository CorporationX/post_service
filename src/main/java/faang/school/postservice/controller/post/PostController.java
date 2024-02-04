package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<Post> createDraft(@Valid @RequestBody PostDto postDto) {
        validatePostDto(postDto);
        Post post = postService.createDraft(postDto);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/publish/{postId} ")
    public String publishPost(@PathVariable long postId) {
        validateId(postId);
        postService.publish(postId);
        return "Post published";
    }

    @PutMapping("/update")
    public String updatePost(@Valid @RequestBody PostDto postDto) {
        validatePostDto(postDto);
        validateId(postDto.getId());
        postService.update(postDto);
        return "Post updated";
    }

    @DeleteMapping("/remove/{postId}")
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

    @GetMapping("/{authorId}")
    public List<PostDto> getPostDraftsByAuthorId(@PathVariable long authorId) {
        validateId(authorId);
        return postService.getDraftsByAuthorId(authorId);
    }

    @GetMapping("/{projectId}")
    public List<PostDto> getPostDraftsByProjectId(@PathVariable long projectId) {
        validateId(projectId);
        return postService.getDraftsByProjectId(projectId);
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


