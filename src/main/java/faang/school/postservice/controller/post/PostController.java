package faang.school.postservice.controller.post;

import faang.school.postservice.service.PostCorrectorService;
import faang.school.postservice.dto.post.TextCheckRequest;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "Post Management", description = "Operations related to posts")
public class PostController {
    private final PostService postService;
    private final PostCorrectorService postCorrecter;

    @PostMapping("/drafts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created successfully")
    })
    public PostOutputDto createPost(@Valid @RequestBody PostCreateDto postCreateDto) {
        log.debug("Creating new post draft {} - Started", postCreateDto);
        PostOutputDto createdPost = postService.createPost(postCreateDto);
        log.info("Creating new post draft {} - Finished", postCreateDto);
        return createdPost;
    }

    @PatchMapping("/{postId}/publish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Published successfully")
    })
    public PostOutputDto publishPost(@NotNull @PathVariable("postId") Long postId) {
        log.debug("Publishing post with id {} - Started", postId);
        PostOutputDto publishedPost = postService.publishPost(postId);
        log.info("Publishing post with id {} - Finished", postId);
        return publishedPost;
    }

    @PutMapping("/{postId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully")
    })
    public PostOutputDto updatePost(@NotNull @PathVariable("postId") Long postId, @Valid @RequestBody PostUpdateDto postUpdateDto) {
        log.debug("Updating post with id {} - Started", postId);
        PostOutputDto updatedPost = postService.updatePost(postId, postUpdateDto);
        log.info("Updating post with id {} - Finished", postId);
        return updatedPost;
    }

    @DeleteMapping("/{postId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully")
    })
    public PostOutputDto deletePost(@NotNull @PathVariable("postId") Long postId) {
        log.debug("Deleting post with id {} - Started", postId);
        PostOutputDto deletedPost = postService.deletePost(postId);
        log.info("Deleting post with id {} - Finished", postId);
        return deletedPost;
    }

    @GetMapping("/{postId}")
    public PostOutputDto getPostById(@NotNull @PathVariable("postId") Long postId) {
        log.debug("Getting post with id {} - Started", postId);
        PostOutputDto foundPost = postService.getPostById(postId);
        log.info("getting post with id {} - Finished", postId);
        return foundPost;
    }

    @GetMapping("/drafts/users/{userId}")
    @Operation(summary = "Gets user`s drafted posts by userID",
            description= "User must exist")
    public List<PostOutputDto> getNotDeletedUserDrafts(@NotNull @PathVariable("userId") Long userId) {
        log.debug("Getting drafted posts for user with id {} - Started", userId);
        List<PostOutputDto> userPosts = postService.getNotDeletedUserDrafts(userId);
        log.info("Getting drafted posts for user with id {} - Finished", userId);
        return userPosts;
    }

    @GetMapping("/drafts/projects/{projectId}")
    @Operation(summary = "Gets project`s drafted posts by projectID",
            description= "Project must exist")
    public List<PostOutputDto> getNotDeletedProjectDrafts(@NotNull @PathVariable("projectId") Long projectId) {
        log.debug("Getting drafted posts for project with id {} - Started", projectId);
        List<PostOutputDto> projectPosts = postService.getNotDeletedProjectDrafts(projectId);
        log.info("Getting drafted posts for project with id {} - Finished", projectId);
        return projectPosts;
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Gets user`s published posts by userID",
            description= "User must exist")
    public List<PostOutputDto> getNotDeletedUserPublished(@NotNull @PathVariable("userId") Long userId) {
        log.debug("Getting published posts for user with id {} - Started", userId);
        List<PostOutputDto> userPosts = postService.getNotDeletedUserPublished(userId);
        log.info("Getting published posts for user with id {} - Finished", userId);
        return userPosts;
    }

    @GetMapping("/projects/{projectId}")
    @Operation(summary = "Gets project`s published posts by projectID",
            description= "Project must exist")
    public List<PostOutputDto> getNotDeletedProjectPublished(@NotNull @PathVariable("projectId") Long projectId) {
        log.debug("Getting published posts for project with id {} - Started", projectId);
        List<PostOutputDto> projectPosts = postService.getNotDeletedProjectPublished(projectId);
        log.info("Getting published posts for project with id {} - Finished", projectId);
        return projectPosts;
    }

    @PostMapping("/check-text")
    public TextCheckRequest checkPostText(@RequestBody TextCheckRequest request) {
        String correctedText = postCorrecter.checkText(request.getText());
        return new TextCheckRequest(correctedText);
    }
}