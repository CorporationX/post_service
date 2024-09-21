package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostDto createDraftPost(@RequestBody @Valid PostDto postDto) {
        return postService.createDraftPost(postDto);
    }

    @PutMapping("/publication")
    public PostDto publishPost(@RequestBody @Valid PostDto postDto) {
        return postService.publishPost(postDto);
    }

    @PutMapping
    public PostDto updatePost(@RequestBody @Valid PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @PutMapping("/{postId}")
    public PostDto softDeletePost(@PathVariable Long postId) {
        return postService.softDeletePost(postId);
    }

    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @GetMapping("/drafts/user/{userId}")
    public List<PostDto> getAllDraftsByAuthorId(@PathVariable Long userId) {
        return postService.getAllDraftsByAuthorId(userId);
    }

    @GetMapping("/drafts/project/{projectId}")
    public List<PostDto> getAllDraftsByProjectId(@PathVariable Long projectId) {
        return postService.getAllDraftsByProjectId(projectId);
    }

    @GetMapping("/user/{userId}")
    public List<PostDto> getAllPublishedPostsByAuthorId(@PathVariable Long userId) {
        return postService.getAllPublishedPostsByAuthorId(userId);
    }

    @GetMapping({"/project/{projectId}"})
    public List<PostDto> getAllPublishedPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllPublishedPostsByProjectId(projectId);
    }
}
