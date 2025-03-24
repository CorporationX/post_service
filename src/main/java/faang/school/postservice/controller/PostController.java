package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
@RequiredArgsConstructor
@Validated
public class PostController {
    private final PostService postService;

    @PostMapping("/drafts")
    public PostDto createDraft(@Valid @RequestBody PostDto postDTO) {
        return postService.createDraft(postDTO);
    }

    @PostMapping("/{id}")
    public PostDto publishPost(@PathVariable long id) {
        return postService.publishPost(id);
    }

    @PutMapping
    public PostDto updatePost(@Valid @RequestBody PostDto postDTO) {
        return postService.updatePost(postDTO);
    }

    @PutMapping("/{id}")
    public PostDto deletePost(@PathVariable long id) {
        return postService.deletePost(id);
    }

    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/drafts/user/{authorId}")
    public List<PostDto> getAllDraftsByAuthorId(@PathVariable long authorId) {
        return postService.getAllDraftsByAuthorId(authorId);
    }

    @GetMapping("/drafts/project/{projectId}")
    public List<PostDto> getAllDraftsByProjectId(@PathVariable long projectId) {
        return postService.getAllDraftsByProjectId(projectId);
    }

    @GetMapping("/user/{authorId}")
    public List<PostDto> getAllPostsByAuthorId(@PathVariable long authorId) {
        return postService.getAllPostsByAuthorId(authorId);
    }

    @GetMapping("/project/{projectId}")
    public List<PostDto> getAllPostsByProjectId(@PathVariable long projectId) {
        return postService.getAllPostsByProjectId(projectId);
    }
}
