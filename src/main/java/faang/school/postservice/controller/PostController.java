package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDTO;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public PostDTO createDraft(@Valid @RequestBody PostDTO postDTO) {
        return postService.createDraft(postDTO);
    }

    @PostMapping("/{postId}")
    public PostDTO publishPost(@PathVariable long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping
    public PostDTO updatePost(@Valid @RequestBody PostDTO postDTO) {
        return postService.updatePost(postDTO);
    }

    @PutMapping("/{postId}")
    public PostDTO deletePost(@PathVariable long postId) {
        return postService.deletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostDTO getPostById(@PathVariable long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/drafts/user/{authorId}")
    public List<PostDTO> getAllDraftsByAuthorId(@PathVariable long authorId) {
        return postService.getAllDraftsByAuthorId(authorId);
    }

    @GetMapping("/drafts/project/{projectId}")
    public List<PostDTO> getAllDraftsByProjectId(@PathVariable long projectId) {
        return postService.getAllDraftsByProjectId(projectId);
    }

    @GetMapping("/user/{authorId}")
    public List<PostDTO> getAllPostsByAuthorId(@PathVariable long authorId) {
        return postService.getAllPostsByAuthorId(authorId);
    }

    @GetMapping("/project/{projectId}")
    public List<PostDTO> getAllPostsByProjectId(@PathVariable long projectId) {
        return postService.getAllPostsByProjectId(projectId);
    }
}
