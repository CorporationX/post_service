package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping("/drafts")
    public PostDto createDraftPost(@RequestBody @Valid PostDto postDto) {
        log.info("Endpoint <createDraftPost>, uri='/posts/drafts' was called successfully");
        return postService.crateDraftPost(postDto);
    }

    @PostMapping("/{id}/publish")
    public PostDto publishPost(@PathVariable("id") long postId) {
        log.info("Endpoint <publishPost>, uri='/posts/{}/publish' was called successfully", postId);
        return postService.publishPost(postId);
    }

    @PutMapping("/change")
    public PostDto updatePost(@RequestBody @Valid PostDto updatePost) {
        log.info("Endpoint <updatePost>, uri='/posts/change' was called successfully");
        return postService.updatePost(updatePost);
    }

    @PutMapping("{id}/soft-delete")
    public PostDto softDelete(@PathVariable("id") long postId) {
        log.info("Endpoint <softDelete>, uri='/posts/{}/soft-delete' was called successfully", postId);
        return postService.softDelete(postId);
    }

    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable("id") long postId) {
        log.info("Endpoint <getPost>, uri='/posts/{}' was called successfully", postId);
        return postService.getPost(postId);
    }

    @GetMapping("drafts/users/{id}")
    public List<PostDto> getUserDrafts(@PathVariable("id") long userId) {
        log.info("Endpoint <getUsersDrafts>, uri='/posts/drafts/users/{}' was called successfully", userId);
        return postService.getUserDrafts(userId);
    }

    @GetMapping("/drafts/projects/{id}")
    public List<PostDto> getProjectDrafts(@PathVariable("id") long projectId) {
        log.info("Endpoint <getProjectDrafts>, uri='/posts/drafts/projects/{}' was called successfully", projectId);
        return postService.getProjectDrafts(projectId);
    }

    @GetMapping("/users/{id}")
    public List<PostDto> getUserPosts(@PathVariable("id") long userId) {
        log.info("Endpoint <getUserPosts>, uri='/posts/users/{}' was called successfully", userId);
        return postService.getUserPosts(userId);
    }

    @GetMapping("/projects/{id}")
    public List<PostDto> getProjectPosts(@PathVariable("id") long projectId) {
        log.info("Endpoint <getProjectPosts>, uri='/posts/projects/{}' was called successfully", projectId);
        return postService.getProjectPosts(projectId);
    }
}