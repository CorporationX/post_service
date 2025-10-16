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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
public class PostController {
    private final UserContext userContext;
    private final PostService postService;

    @PostMapping("/posts/newpost")
    public ResponseEntity<PostDto> createPost(@RequestBody @Valid CreatePostDto createPostDto) throws Exception {
        return ResponseEntity.ok(postService.createPost(userContext.getUserId(), createPostDto));
    }

    @PutMapping("/posts/publishing")
    public boolean publishPost(@RequestParam("id") long postId) {
        return postService.publishPost(userContext.getUserId(), postId);
    }

    @PutMapping("/posts/updating")
    public PostDto updatePost(@RequestParam("id") long postId, @RequestBody @Valid UpdatePostDto updatePostDto) {
        return postService.updatePost(postId, userContext.getUserId(), updatePostDto);
    }

    @DeleteMapping("/posts")
    public boolean deletePost(@RequestParam("id") long postId) {
        return postService.deletePost(userContext.getUserId(), postId);
    }

    @GetMapping("/posts")
    public PostDto getPostById(@RequestParam("id") long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/posts/unpublished/byauthor")
    public List<PostDto> getAllUnpublishedPostsByAuthor() {
        return postService.getAllUnpublishedPostsByAuthor(userContext.getUserId());
    }

    @GetMapping("/posts/unpublished/byproject")
    public List<PostDto> getAllUnpublishedPostsByProject(@RequestParam("project") long projectId) {
        return postService.getAllUnpublishedPostsByProject(projectId);
    }

    @GetMapping("/posts/published/byauthor")
    public List<PostDto> getAllPublishedPostsByAuthor(@RequestParam("id") long authorId) {
        return postService.getAllPublishedPostsByAuthor(authorId);
    }

    @GetMapping("/posts/published/byproject")
    public List<PostDto> getAllPublishedPostsByProject(@RequestParam("project") long projectId) {
        return postService.getAllPublishedPostsByProject(projectId);
    }
}
