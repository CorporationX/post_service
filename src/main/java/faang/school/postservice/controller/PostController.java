package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "Creating a draft post",
            description = "Exactly one author user or project creates a draft of the post"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post created"),
            @ApiResponse(responseCode = "500", description = "There is no access to the database of users or projects")
    })
    @PostMapping("/create")
    public PostDto createPost(@RequestBody @Valid PostDto postDto) {
        return postService.createPost(postDto);
    }

    @Operation(summary = "Publishing a post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post published"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "500", description = "Error when publishing a post")
    })
    @PutMapping("/publish/{postId}")
    public PostDto publishPost(@PathVariable @Positive(message = "Id must be greater than zero") long postId) {
        return postService.publishPost(postId);
    }

    @Operation(
            summary = "Update existing post",
            description = "Gets PostDto and updates existing post"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "500", description = "Error updating the post")
    })
    @PutMapping("/{postId}")
    public PostDto updatePost(@PathVariable long postId, @RequestBody @Valid PostDto postDto) {
        return postService.updatePost(postId, postDto);
    }

    @Operation(summary = "Deleting a post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "500", description = "Error when deleting a post")
    })
    @DeleteMapping("/{postId}")
    public PostDto deletePost(@PathVariable @Positive(message = "Id must be greater than zero") long postId) {
        return postService.deletePost(postId);
    }

    @Operation(summary = "Getting a post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post received"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "500", description = "Error when receiving a post")
    })
    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable @Positive(message = "Id must be greater than zero") long postId) {
        return postService.getPostById(postId);
    }

    @Operation(
            summary = "Getting drafts of posts by user ID",
            description = "Getting all drafts of not deleted posts authored by user ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Drafts post received"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "500", description = "Error when receiving drafts post by user ID")
    })
    @GetMapping("/author/{id}/drafts")
    public List<PostDto> getDraftsByAuthorId(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getDraftsByAuthorId(id);
    }

    @Operation(
            summary = "Getting drafts of posts by project ID",
            description = "Getting all drafts of non deleted posts authored by project ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Drafts post received"),
            @ApiResponse(responseCode = "400", description = "Invalid project ID"),
            @ApiResponse(responseCode = "500", description = "Error when receiving drafts post by project ID")
    })
    @GetMapping("/project/{id}/drafts")
    public List<PostDto> getDraftsByProjectId(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getDraftsByProjectId(id);
    }

    @Operation(
            summary = "Getting published posts by user ID",
            description = "Getting all published of not deleted posts authored by user ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Published post received"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "500", description = "Error when receiving published post by user ID")
    })
    @GetMapping("/author/{id}")
    public List<PostDto> getPostsByAuthorId(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getPostsByAuthorId(id);
    }

    @Operation(
            summary = "Getting published posts by project ID",
            description = "Getting all published of not deleted posts authored by project ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Published post received"),
            @ApiResponse(responseCode = "400", description = "Invalid project ID"),
            @ApiResponse(responseCode = "500", description = "Error when receiving published post by project ID")
    })
    @GetMapping("/project/{id}")
    public List<PostDto> getPostsByProjectId(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getPostsByProjectId(id);
    }

}