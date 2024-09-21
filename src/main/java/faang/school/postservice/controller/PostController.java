package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.request.PostCreationRequest;
import faang.school.postservice.dto.post.request.PostUpdatingRequest;
import faang.school.postservice.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "Creating new Post")
    @PostMapping
    public PostDto createPost(@Valid
                              @RequestBody PostCreationRequest request) {
        return postService.create(request);
    }

    @Operation(summary = "Publishing the post")
    @PatchMapping("/publish/{postId}")
    public PostDto publishThePost(@Positive
                                  @PathVariable Long postId) {
        return postService.publish(postId);
    }

    @Operation(summary = "Updating the content")
    @PatchMapping("/update/{postId}")
    public PostDto updatePost(@Positive
                              @PathVariable Long postId, @Valid @RequestBody PostUpdatingRequest request) {
        return postService.update(postId, request);
    }

    @Operation(summary = "Deleting the post")
    @DeleteMapping("/{postId}")
    public PostDto deletePost(@Positive
                              @PathVariable Long postId) {
        return postService.remove(postId);
    }

    @Operation(summary = "Getting post by id")
    @GetMapping("/{postId}")
    public PostDto getPostById(@Positive
                               @PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    @Operation(summary = "Getting all unpublished posts by authorId")
    @GetMapping("/unpublished-by-authorId/{authorId}")
    public List<PostDto> getAllUnpublishedPostsByAuthorId(@Positive
                                                          @PathVariable Long authorId) {
        return postService.getUnpublishedPostsByAuthorId(authorId);
    }

    @Operation(summary = "Getting all unpublished posts by projectId")
    @GetMapping("/unpublished-by-projectId/{projectId}")
    public List<PostDto> getAllUnpublishedPostsByProjectId(@Positive
                                                           @PathVariable Long projectId) {
        return postService.getUnpublishedPostsByProjectId(projectId);
    }

    @Operation(summary = "Getting all published posts by authorId")
    @GetMapping("/published-by-authorId/{authorId}")
    public List<PostDto> getAllPublishedPostsByAuthorId(@Positive
                                                        @PathVariable Long authorId) {
        return postService.getPublishedPostsByAuthorId(authorId);
    }

    @Operation(summary = "Getting all published posts by projectId")
    @GetMapping("/published-by-projectId/{projectId}")
    public List<PostDto> getAllPublishedPostsByProjectId(@Positive
                                                         @PathVariable Long projectId) {
        return postService.getPublishedPostsByProjectId(projectId);
    }
}
