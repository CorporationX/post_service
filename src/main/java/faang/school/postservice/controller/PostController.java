package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.request.PostCreationRequest;
import faang.school.postservice.dto.post.request.PostUpdatingRequest;
import faang.school.postservice.dto.resource.ResourceObjectResponse;
import faang.school.postservice.model.post.PostCreator;
import faang.school.postservice.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;

    @Operation(summary = "Creating new Post")
    @PostMapping
    public PostDto createPost(@Valid @ModelAttribute PostCreationRequest request) {
        return postService.create(request);
    }

    @Operation(summary = "Publishing the post")
    @PutMapping("/{postId}/publish")
    public PostDto publishPost(@Positive
                               @PathVariable Long postId) {
        return postService.publish(postId);
    }

    @Operation(summary = "Updating the content")
    @PatchMapping("/{postId}")
    public PostDto updatePost(@Positive
                              @PathVariable Long postId,
                              @Valid @ModelAttribute PostUpdatingRequest request) {
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

    @Operation(summary = "Getting posts by creator and publish status")
    @GetMapping("/{creatorId}/creator")
    public List<PostDto> getPostsByCreator(@Positive @PathVariable Long creatorId,
                                           @RequestParam PostCreator creator,
                                           @RequestParam Boolean publishStatus) {
        return postService.getPostsByCreatorAndPublishedStatus(creatorId, creator, publishStatus);
    }

    @GetMapping("/{postId}/resources")
    public ResponseEntity<List<byte[]>> getPostResourcesById(@Positive @PathVariable Long postId) {
        List<ResourceObjectResponse> files = postService.getResourcesByPostId(postId);
        return ResponseEntity.ok()
                .body(files.stream()
                        .map(file -> {
                            try {
                                return file.content().readAllBytes();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList());
    }
}
