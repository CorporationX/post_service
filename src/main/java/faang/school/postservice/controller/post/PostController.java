package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.util.post.PostValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PostValidator postValidator;

    @PostMapping("/drafts")
    public PostDto createDraft(@Valid @RequestBody PostDto postDto) {
        postValidator.validateIds(postDto);
        return postService.createDraft(postDto);
    }

    @PostMapping("/drafts/{postId}")
    public PostDto publishPost(@PathVariable long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@PathVariable long postId, @Valid @RequestBody PostDto postDto) {
        postValidator.validateIds(postDto);
        return postService.updatePost(postId, postDto);
    }

    @DeleteMapping("/{postId}")
    public PostDto deletePost(@PathVariable long postId) {
        return postService.deletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostDto findPostById(@PathVariable long postId) {
        return postService.findPostById(postId);
    }

    @GetMapping("/drafts/authors/{authorId}")
    public List<PostDto> findDraftsByAuthorId(@PathVariable long authorId) {
        return postService.findDraftsByAuthorId(authorId);
    }

    @GetMapping("/drafts/projects/{projectId}")
    public List<PostDto> findDraftsByProjectId(@PathVariable long projectId) {
        return postService.findDraftsByProjectId(projectId);
    }

    @GetMapping("/authors/{authorId}")
    public List<PostDto> findPostsByAuthorId(@PathVariable long authorId) {
        return postService.findPostsByAuthorId(authorId);
    }

    @GetMapping("/projects/{projectId}")
    public List<PostDto> findPostsByProjectId(@PathVariable long projectId) {
        return postService.findPostsByProjectId(projectId);
    }
}
