package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @Deprecated
    public PostDto createDraft(@RequestBody @Valid PostDto postDto) {
        return postService.createDraft(postDto);
    }

    @PutMapping("/{postId}/publish")
    public PostDto publish(@PathVariable Long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping("/{postId}")
    @Deprecated
    public PostDto update(@PathVariable Long postId, @RequestBody @Valid PostDto postDto) {
        return postService.updatePost(postId, postDto);
    }

    @DeleteMapping("/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.deletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/drafts/user/{userId}")
    public List<PostDto> getUserDrafts(@PathVariable Long userId) {
        return postService.getAllDraftsByAuthorId(userId);
    }

    @GetMapping("/drafts/project/{projectId}")
    public List<PostDto> getProjectDrafts(@PathVariable Long projectId) {
        return postService.getAllDraftsByProjectId(projectId);
    }

    @GetMapping("/user/{userId}")
    public List<PostDto> getUserPublished(@PathVariable Long userId) {
        return postService.getAllPostsByAuthorId(userId);
    }

    @GetMapping("/project/{projectId}")
    public List<PostDto> getProjectPublished(@PathVariable Long projectId) {
        return postService.getAllPostsByProjectId(projectId);
    }
}
