package faang.school.postservice.controller.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Tag(name = "Posts", description = "Endpoints for managing posts")
public class PostController {
    private final PostService postService;
    private final UserContext userContext;

    @Operation(summary = "Create a post draft")
    @PostMapping
    public PostDto create(@RequestPart @Valid PostDto postDto,
                          @RequestPart(required = false) MultipartFile[] images) {
        return postService.create(postDto, images);
    }

    @Operation(summary = "Get post by id")
    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable @Min(1) long postId) {
        long userId = userContext.getUserId();
        return postService.getPostById(userId, postId);
    }
//    @Operation(summary = "Get post from redis")
//    @GetMapping("/redis/{postId}")
//    public PostRedis getPostByIdFromRedis(@PathVariable long postId){
//        return postService.(postId);
//    }

    @Operation(summary = "Update existing post")
    @PutMapping
    public PostDto update(@RequestPart @Valid PostDto postDto,
                          @RequestPart(required = false) MultipartFile[] images) {
        return postService.update(postDto, images);
    }

    @Operation(summary = "Publish created post draft")
    @PutMapping("/{postId}/published")
    public PostDto publish(@PathVariable @Min(1) long postId) {
        return postService.publish(postId);
    }

    @Operation(summary = "Delete a post")
    @DeleteMapping("/{postId}")
    public void delete(@PathVariable @Min(1) long postId) {
        postService.delete(postId);
    }

    @Operation(summary = "Get created post draft by user id")
    @GetMapping("/user/{authorId}")
    public List<PostDto> getCreatedPostsByAuthorId(@PathVariable @Min(1) long authorId) {
        return postService.getCreatedPostsByAuthorId(authorId);
    }

    @Operation(summary = "Get created post draft by project id")
    @GetMapping("/project/{projectId}")
    public List<PostDto> getCreatedPostsByProjectId(@PathVariable @Min(1) long projectId) {
        return postService.getCreatedPostsByProjectId(projectId);
    }

    @Operation(summary = "Get published post by user id")
    @GetMapping("/user/{authorId}/published")
    public List<PostDto> getPublishedPostsByAuthorId(@PathVariable @Min(1) long authorId) {
        long userId = userContext.getUserId();
        return postService.getPublishedPostsByAuthorId(userId, authorId);
    }

    @Operation(summary = "Get published post by project id")
    @GetMapping("/project/{projectId}/published")
    public List<PostDto> getPublishedPostsByProjectId(@PathVariable @Min(1) long projectId) {
        long userId = userContext.getUserId();
        return postService.getPublishedPostsByProjectId(userId, projectId);
    }

    @Operation(summary = "Attach media file to post")
    @PutMapping("/{postId}")
    public ResourceDto attachMedia(@PathVariable @Positive(message = "Post id must be positive number") long postId,
                                   @RequestParam MultipartFile file) {
        return postService.attachMedia(postId, file);
    }
}
