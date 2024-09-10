package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.*;
import faang.school.postservice.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "Post", description = "APIs for managing posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "Create a new draft post", description = "Creates a new draft post and returns its details")
    public PostDto createDraft(@Valid @ModelAttribute DraftPostDto draft) {
        return postService.createPostDraft(draft);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Publish a post", description = "Publishes a draft making it a post")
    public PostDto publishPost(@PathVariable long id) {
        return postService.publishPost(id);
    }

    @PatchMapping
    @Operation(summary = "Update a post",description = "Allows to update an existing post or draft")
    public PostDto updatePost(@Valid @ModelAttribute UpdatablePostDto updatablePost) {
        return postService.updatePost(updatablePost);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Search post", description = "Getting a post or draft if it hasn't been deleted")
    public PostDto getPost(@PathVariable long id) {
        return postService.findPost(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete post", description = "Delete a post or draft if they have not been deleted before")
    public void deletePost(@PathVariable long id) {
        postService.deletePost(id);
    }

    @GetMapping("/list")
    @Operation(summary = "Getting post", description = "Allows you to get all drafts or posts from a specified creator")
    public List<PostDto> getPosts(
            @RequestParam(value = "author_id", required = false) Long authorId,
            @RequestParam(value = "project_id", required = false) Long projectId,
            @RequestParam(value = "post_status") PostStatus postStatus
    ) {

        GetPostsDto getPostRequest = new GetPostsDto(
                authorId,
                projectId,
                postStatus
        );

        return postService.getPosts(getPostRequest);
    }
}
