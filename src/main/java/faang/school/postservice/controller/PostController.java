package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "Post", description = "Post API")
public class PostController {
    private final PostService postService;
    private final PostValidator postValidator;

    @PostMapping("/create")
    @Operation(summary = "Create Post")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto createPost(@RequestBody @Valid PostDto post) {
        postValidator.validationOfPostCreatorIds(post);
        return postService.createPost(post);
    }

    @PutMapping("{postId}/publish")
    @Operation(summary = "Publish Post")
    @ResponseStatus(HttpStatus.OK)
    public PostDto publishPost(@PathVariable @Valid Long postId) {
        return postService.publishPost(postId);
    }

    @PatchMapping("/update")
    @Operation(summary = "Update Post")
    @ResponseStatus(HttpStatus.OK)
    public PostDto updatePost(@RequestBody @Valid PostDto post) {
        postValidator.validationOfPostCreatorIds(post);

        return postService.updatePost(post);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete Post")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean deletePost(@PathVariable Long postId) {
        return postService.softDeletePost(postId);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Get Post by Id")
    @ResponseStatus(HttpStatus.OK)
    public PostDto getPost(@PathVariable @Valid Long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/users/{authorId}/drafts")
    @Operation(summary = "Get not deleted drafts by author id")
    @ResponseStatus(HttpStatus.OK)
    public List<PostDto> getDraftsByAuthorId(@PathVariable @Valid Long authorId) {
        validateId(authorId);
        return postService.getNotDeletedDraftsByAuthorId(authorId);
    }

    @GetMapping("/projects/{projectId}/drafts")
    @Operation(summary = "Get not deleted drafts by project id")
    @ResponseStatus(HttpStatus.OK)
    public List<PostDto> getDraftsByProjectId(@PathVariable @Valid Long projectId) {
        validateId(projectId);
        return postService.getNotDeletedDraftsByProjectId(projectId);
    }

    @GetMapping("/users/{authorId}/posts")
    @Operation(summary = "Get not deleted published posts by author id")
    @ResponseStatus(HttpStatus.OK)
    public List<PostDto> getPublishedPostsByAuthorId(@PathVariable @Valid Long authorId) {
        validateId(authorId);
        return postService.getNotDeletedPublishedPostsByAuthorId(authorId);
    }

    @GetMapping("/projects/{projectId}/posts")
    @Operation(summary = "Get not deleted published posts by project id")
    @ResponseStatus(HttpStatus.OK)
    public List<PostDto> getPublishedPostsByProjectId(@PathVariable @Valid Long projectId) {
        validateId(projectId);
        return postService.getNotDeletedPublishedPostsByProjectId(projectId);
    }

    private void validateId(Long id) {
        if (id < 0) {
            throw new DataValidationException("Id cannot be negative");
        }
    }
}
