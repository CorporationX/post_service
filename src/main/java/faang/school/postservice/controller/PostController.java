package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
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

    @PostMapping("/creating")
    public void createPost(@RequestBody @Valid PostDto postDto) {
        validateCreatorId(postDto);
        postService.createPost(postDto);
    }

    @PutMapping("/publishing/{postId}")
    public void publishPost(@PathVariable long postId) {
        postService.publishPost(postId);
    }

    @PutMapping("/updating")
    public void updatePost(@RequestBody @Valid PostDto postDto) {
        postService.updatePost(postDto);
    }

    @DeleteMapping("/deleted/{postId}")
    public void deletePost(@PathVariable long postId) {
        postService.deletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPost(@PathVariable long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/drafts/author/{authorId}")
    public List<PostDto> getPostDraftsByAuthorId(@PathVariable long authorId) {
        return postService.getPostDraftsByAuthorId(authorId);
    }

    @GetMapping("/drafts/project/{projectId}")
    public List<PostDto> getPostDraftsByProjectId(@PathVariable long projectId) {
        return postService.getPostDraftsByProjectId(projectId);
    }

    @GetMapping("/published/author/{authorId}")
    public List<PostDto> getPostPublishedByAuthorId(@PathVariable long authorId) {
        return postService.getPostPublishedByAuthorId(authorId);
    }

    @GetMapping("/published/project/{projectId}")
    public List<PostDto> getPostPublishedByProjectId(@PathVariable long projectId) {
        return postService.getPostPublishedByProjectId(projectId);
    }

    private void validateCreatorId(PostDto post) {
        Long authorId = post.getAuthorId();
        Long projectId = post.getProjectId();

        if (authorId == null && projectId == null) {
            throw new NullPointerException("ID of the author of the post should not be null");
        }
        if (authorId != null && projectId != null) {
            throw new IllegalArgumentException("Сan be only one author");
        }

        if (authorId != null) {
            post.setAuthorId(authorId);
        } else {
            post.setProjectId(projectId);
        }
    }
}
