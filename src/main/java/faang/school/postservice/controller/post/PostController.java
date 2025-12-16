package faang.school.postservice.controller.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/v1/posts")
@Validated
@RequiredArgsConstructor
public class PostController {
    private final UserContext userContext;
    private final PostService postService;

    @PostMapping("/")
    public ResponseEntity<PostDto> createPost(@RequestBody @Valid CreatePostDto createPostDto) throws Exception {
        return ResponseEntity.ok(postService.createPost(userContext.getUserId(), createPostDto));
    }

    @PutMapping("/{id}/publish")
    public void publishPost(@PathVariable("id") long postId) {
        postService.publishPost(userContext.getUserId(), postId);
    }

    @PutMapping("/{id}")
    public PostDto updatePost(@PathVariable("id") long postId, @RequestBody @Valid UpdatePostDto updatePostDto) {
        return postService.updatePost(postId, userContext.getUserId(), updatePostDto);
    }

    @DeleteMapping("/{id}")
    public boolean deletePost(@PathVariable("id") long postId) {
        return postService.deletePost(userContext.getUserId(), postId);
    }

    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable("id") long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/unpublished")
    public List<PostDto> getAllUnpublishedPostsByAuthor() {
        return postService.getAllUnpublishedPostsByAuthor(userContext.getUserId());
    }

    @GetMapping("/unpublished/by-project/{project}")
    public List<PostDto> getAllUnpublishedPostsByProject(@PathVariable("project") long projectId) {
        return postService.getAllUnpublishedPostsByProject(projectId);
    }

    @GetMapping("/published/by-author/{author}")
    public List<PostDto> getAllPublishedPostsByAuthor(@PathVariable("author") long authorId) {
        return postService.getAllPublishedPostsByAuthor(authorId);
    }

    @GetMapping("/published/by-project/{project}")
    public List<PostDto> getAllPublishedPostsByProject(@PathVariable("project") long projectId) {
        return postService.getAllPublishedPostsByProject(projectId);
    }
}
