package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public PostDto createDraftPost(@Valid PostDto postDto) {
        return postService.createDraftPost(postDto);
    }

    @PutMapping("/publish")
    public PostDto publishPost(@Valid PostDto postDto) {
        return postService.publishPost(postDto);
    }

    @PutMapping("/update")
    public PostDto updatePost(@Valid PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @DeleteMapping("/delete")
    public PostDto softDeletePost(@Valid PostDto postDto) {
        return postService.softDeletePost(postDto);
    }

    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @GetMapping("/drafts/author/{authorId}")
    public List<PostDto> getAllDraftsByAuthorId(@PathVariable Long authorId) {
        return postService.getAllDraftsByAuthorId(authorId);
    }

    @GetMapping("/drafts/project/{projectId}")
    public List<PostDto> getAllDraftsByProjectId(@PathVariable Long projectId) {
        return postService.getAllDraftsByProjectId(projectId);
    }

    @GetMapping("/published/author/{authorId}")
    public List<PostDto> getAllPublishedPostsByAuthorId(@PathVariable Long authorId) {
        return postService.getAllPublishedPostsByAuthorId(authorId);
    }

    @GetMapping({"/published/project/{projectId}"})
    public List<PostDto> getAllPublishedPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllPublishedPostsByProjectId(projectId);
    }
}
