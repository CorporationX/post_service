package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        if (validatePost(post)) {
            return postService.createPost(post);
        }
        throw new DataValidationException("AuthorId or ProjectId cannot be null");
    }

    @GetMapping("/{authorId}/drafts")
    @Operation(summary = "Get not deleted drafts by author id")
    @ResponseStatus(HttpStatus.OK)
    public List<PostDto> getNotDeletedDraftsByAuthorId(@RequestParam @Valid Long authorId) {
        validateId(authorId);
        return postService.getNotDeletedDraftsByAuthorId(authorId);
    }

    @GetMapping("/{projectId}/drafts")
    @Operation(summary = "Get not deleted drafts by project id")
    @ResponseStatus(HttpStatus.OK)
    public List<PostDto> getNotDeletedDraftsByProjectId(@RequestParam @Valid Long projectId) {
        validateId(projectId);
        return postService.getNotDeletedDraftsByProjectId(projectId);
    }

    private void validateId(Long id) {
        if (id < 0) {
            throw new DataValidationException("Id cannot be negative");
        }
    }

    private boolean validatePost(PostDto post) {
        return post.getAuthorId() != null || post.getProjectId() != null;
    }
}
