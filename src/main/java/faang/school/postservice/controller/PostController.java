package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
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
    private final UserContext userContext;

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

    @GetMapping("/drafts/author")
    public List<PostDto> getPostDraftsByAuthorId() {
        validateAuthorId();
        return postService.getPostDraftsByAuthorId(userContext.getUserId());
    }

    @GetMapping("/drafts/project")
    public List<PostDto> getPostDraftsByProjectId() {
        validateProjectId();
        return postService.getPostDraftsByProjectId(userContext.getProjectId());
    }

    @GetMapping("/published/author")
    public List<PostDto> getPostPublishedByAuthorId() {
        validateAuthorId();
        return postService.getPostPublishedByAuthorId(userContext.getUserId());
    }

    @GetMapping("/published/project")
    public List<PostDto> getPostPublishedByProjectId() {
        validateProjectId();
        return postService.getPostPublishedByProjectId(userContext.getProjectId());
    }

    private void validateCreatorId(PostDto post) {
        Long xUserId = userContext.getUserId();
        Long xProjectId = userContext.getProjectId();

        if (xUserId == null && xProjectId == null) {
            throw new NullPointerException("ID of the author of the post should not be null");
        }
        if (xUserId != null && xProjectId != null) {
            throw new IllegalArgumentException("Сan be only one author");
        }

        if (xUserId != null) {
            post.setAuthorId(xUserId);
        } else {
            post.setProjectId(xProjectId);
        }
    }

    private void validateAuthorId() {
        Long xUserId = userContext.getUserId();
        Long xProjectId = userContext.getProjectId();

        if (xUserId == null && xProjectId == null) {
            throw new NullPointerException("ID of the author of the post should not be null");
        }
        if (xUserId != null && xProjectId != null) {
            throw new IllegalArgumentException("Сan be only one author");
        }
        if (xProjectId != null) {
            throw new IllegalArgumentException("Viewing is prohibited");
        }
    }

    private void validateProjectId() {
        Long xUserId = userContext.getUserId();
        Long xProjectId = userContext.getProjectId();

        if (xUserId == null && xProjectId == null) {
            throw new NullPointerException("ID of the project of the post should not be null");
        }
        if (xUserId != null && xProjectId != null) {
            throw new IllegalArgumentException("Сan be only one author");
        }
        if (xUserId != null) {
            throw new IllegalArgumentException("Viewing is prohibited");
        }
    }
}
