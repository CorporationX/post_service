package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "Post", description = "Post API")
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    @Operation(summary = "Create Post")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto createPost(@RequestBody @Valid PostDto post) {
        validate(post);

        return postService.createPost(post);
    }

    @PutMapping("{postId}/publish")
    @Operation(summary = "Publish Post")
    @ResponseStatus(HttpStatus.OK)
    public PostDto publishPost(@PathVariable Long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping("/update")
    @Operation(summary = "Update Post")
    @ResponseStatus(HttpStatus.OK)
    public PostDto updatePost(@RequestBody @Valid PostDto post) {
        validate(post);

        return postService.updatePost(post);
    }

    private void validate(PostDto post) {
        if (post.getAuthorId() == null && post.getProjectId() == null) {
            throw new DataValidationException("AuthorId or ProjectId cannot be null");
        }
    }
}
