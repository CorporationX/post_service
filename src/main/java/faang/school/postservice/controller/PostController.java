package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validation.PostValidator;
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
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostValidator postValidator;

    @PostMapping("/draft")
    public PostDto createDraftPost(@RequestBody @Valid PostDto postDto) {
        postValidator.validateAuthorCount(postDto);
        return postService.createDraftPost(postDto);
    }

    @PutMapping("/drafts/{id}")
    public PostDto publishDraftPost(@PathVariable Long id) {
        return postService.publishDraftPost(id);
    }

    @PutMapping("/{id}")
    public PostDto updatePost(@RequestBody @Valid PostDto postDto, @PathVariable Long id) {
        return postService.updatePost(postDto, id);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }

    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable long id) {
        return postService.getPost(id);
    }

    @GetMapping("/drafts/user/{userId}")
    public List<PostDto> getUserDrafts(@PathVariable Long userId) {
        return postService.getUserDrafts(userId);
    }

    @GetMapping("drafts/project/{projectId}")
    public List<PostDto> getProjectDrafts(@PathVariable Long projectId) {
        return postService.getProjectDrafts(projectId);
    }

    @GetMapping("user/{userId}")
    public List<PostDto> getUserPosts(@PathVariable Long userId) {
        return postService.getUserPosts(userId);
    }

    @GetMapping("project/{projectId}")
    public List<PostDto> getProjectPosts(@PathVariable Long projectId) {
        return postService.getProjectPosts(projectId);
    }
}

