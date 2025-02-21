package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validation.PostDtoValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@RequiredArgsConstructor
@RequestMapping("/post")
@RestController
@Tag(name = "Post Controller", description = "APIs for managing posts")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final PostDtoValidator postDtoValidator;

    @Operation(
            summary = "Create a draft post",
            description = "Users can create text-only posts, similar to LinkedIn. These posts will form a news feed " +
                    "for followers. This is essentially a text block with any information the user wants to place in " +
                    "their profile. By default, a post is created in draft state and can be published later."
    )
    @PostMapping("/draft")
    public PostDto createDraft(@RequestBody @Valid PostDto postDto) {
        postDtoValidator.isValid(postDto, null);
        Post receivedDraft = postMapper.toEntity(postDto);
        Post createdDraft = postService.createDraft(receivedDraft);
        return postMapper.toDto(createdDraft);
    }

    @Operation(
            summary = "Publish a Draft post by ID",
            description = "Publish any existing post. A post that has already been published cannot be published " +
                    "again. Remember the publication date."
    )
    @PostMapping("/publish/{postId}")
    public PostDto publish(@Parameter(description = "ID of the post to be published")
                           @PathVariable Long postId) {
        Post publishedPost = postService.publish(postId);
        return postMapper.toDto(publishedPost);
    }

    @Operation(
            summary = "Update existing post",
            description = "Update the text of an existing post. The author of the post cannot be changed or removed."
    )
    @PatchMapping
    public PostDto update(@RequestBody PostDto postDto) {
        Post receivedPostData = postMapper.toEntity(postDto);
        Post updatedPost = postService.update(receivedPostData);
        return postMapper.toDto(updatedPost);
    }

    @Operation(
            summary = "Delete existing post by ID",
            description = "Soft delete a post by ID. The post is not removed from the database but marked as deleted " +
                    "and continues to be stored."
    )
    @DeleteMapping("/{postId}")
    public void delete(@Parameter(description = "ID of the post to be deleted")
                       @PathVariable Long postId) {
        postService.delete(postId);
    }

    @Operation(
            summary = "Get a post by ID",
            description = "Retrieve a post by its ID."
    )
    @GetMapping("/{postId}")
    public PostDto get(@Parameter(description = "ID of the post to be retrieved")
                       @PathVariable Long postId) {
        Post currentPost = postService.get(postId);
        return postMapper.toDto(currentPost);
    }

    @Operation(
            summary = "Get draft posts by author ID ordered by date created, descending",
            description = "Retrieve all non-deleted draft posts authored by the user with the given ID. Posts are " +
                    "sorted by creation date from newest to oldest."
    )
    @GetMapping("/draft/user/{userId}")
    public List<PostDto> getDraftsByAuthorId(@Parameter(description = "ID of the author")
                                             @PathVariable Long userId) {
        List<Post> draftsByAuthorId = postService.getDraftsByAuthorId(userId);
        return postMapper.toDto(draftsByAuthorId);
    }

    @Operation(
            summary = "Get draft posts by project ID ordered by date created, descending",
            description = "Retrieve all non-deleted draft posts authored by the project with the given ID. Posts are " +
                    "sorted by creation date from newest to oldest."
    )
    @GetMapping("/draft/project/{projectId}")
    public List<PostDto> getDraftsByProjectId(@Parameter(description = "ID of the project")
                                              @PathVariable Long projectId) {
        List<Post> draftsByProjectId = postService.getDraftsByProjectId(projectId);
        return postMapper.toDto(draftsByProjectId);
    }

    @Operation(
            summary = "Get posts by author ID ordered by date published, descending",
            description = "Retrieve all non-deleted published posts authored by the user with the given ID. Posts " +
                    "are sorted by publication date from newest to oldest."
    )
    @GetMapping("/user/{userId}")
    public List<PostDto> getPostsByAuthorId(@Parameter(description = "ID of the author")
                                            @PathVariable Long userId) {
        List<Post> postsByAuthorId = postService.getPostsByAuthorId(userId);
        return postMapper.toDto(postsByAuthorId);
    }

    @Operation(
            summary = "Get posts by project ID ordered by date published, descending",
            description = "Retrieve all non-deleted published posts authored by the project with the given ID. Posts " +
                    "are sorted by publication date from newest to oldest."
    )
    @GetMapping("/project/{projectId}")
    public List<PostDto> getPostsByProjectId(@Parameter(description = "ID of the project")
                                             @PathVariable Long projectId) {
        List<Post> postsByProjectId = postService.getPostsByProjectId(projectId);
        return postMapper.toDto(postsByProjectId);
    }
}