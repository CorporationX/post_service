package faang.school.postservice.controller;

import faang.school.postservice.dto.posts.PostDto;
import faang.school.postservice.dto.posts.PostSaveDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;
    private final PostValidator postValidator;

    private static final String ID_PATH = "/{id}";
    private static final String AUTHOR_ID_PATH = "/{authorId}";
    private static final String PROJECT_ID_PATH = "/{projectId}";
    private static final String USERS_PATH = "/users";
    private static final String PROJECTS_PATH = "/projects";
    private static final String SAVED_PATH = "/saved";
    private static final String PUBLISHED_PATH = "/published";
    private static final String NOT_PUBLISHED_BY_PROJECT_ID_PATH = PROJECTS_PATH + PROJECT_ID_PATH + SAVED_PATH;
    private static final String PUBLISHED_BY_PROJECT_ID_PATH = PROJECTS_PATH + PROJECT_ID_PATH + PUBLISHED_PATH;
    private static final String NOT_PUBLISHED_BY_AUTHOR_ID_PATH = USERS_PATH + AUTHOR_ID_PATH + SAVED_PATH;
    private static final String PUBLISHED_BY_AUTHOR_ID_PATH = USERS_PATH + AUTHOR_ID_PATH + PUBLISHED_PATH;
    private static final String PUBLISH_ID_PATH = ID_PATH + "/publish";

    @PostMapping
    @Operation(
            summary = "Create post draft",
            description = "Creates a new post draft. Author must be either a user or a project (not both). " +
                    "Content cannot be empty."
    )
    public ResponseEntity<PostDto> create(@NotNull @RequestBody PostSaveDto postSaveDto) {
        postValidator.validatePost(postSaveDto);
        log.info("POST /api/v1/posts — request to create post: {}", postSaveDto);
        return new ResponseEntity<>(postService.create(postSaveDto), HttpStatus.CREATED);
    }

    @GetMapping(ID_PATH)
    @Operation(
            summary = "Get post by ID",
            description = "Returns any post by its ID"
    )
    public ResponseEntity<PostDto> getPost(@PathVariable long id) {
        log.info("GET /api/v1/posts/{} — request to get post by id", id);
        return ResponseEntity.ok(postService.getPost(id));
    }

    @PutMapping(ID_PATH)
    @Operation(
            summary = "Update post",
            description = "Updates an existing post content. Author cannot be changed or removed."
    )
    public ResponseEntity<PostDto> update(@PathVariable long id, @NotNull @RequestBody PostSaveDto postSaveDto) {
        postValidator.validatePost(postSaveDto);
        log.info("PUT /api/v1/posts/{} — request to update post: {}", id, postSaveDto);
        return ResponseEntity.ok(postService.update(id, postSaveDto));
    }

    @PostMapping(PUBLISH_ID_PATH)
    @Operation(
            summary = "Publish post",
            description = "Publishes an existing post. Cannot publish an already published post. " +
                    "Publication date is recorded automatically."
    )
    public ResponseEntity<String> publish(@PathVariable long id) {
        log.info("POST /api/v1/posts/{}/publish — request to publish post", id);
        postService.publish(id);
        return ResponseEntity.ok("Пост успешно опубликован!");
    }

    @DeleteMapping(ID_PATH)
    @Operation(
            summary = "Delete post",
            description = "Deletes an existing post by its ID"
    )
    public ResponseEntity<String> delete(@PathVariable long id) {
        log.info("DELETE /api/v1/posts/{} — request to delete post", id);
        postService.delete(id);
        return ResponseEntity.ok("Пост успешно удален!");
    }

    @GetMapping(NOT_PUBLISHED_BY_AUTHOR_ID_PATH)
    @Operation(
            summary = "Get user's post drafts",
            description = "Returns all non-deleted post drafts by user ID, " +
                    "sorted by creation date from newest to oldest"
    )
    public ResponseEntity<List<PostDto>> getSavedPostsByAuthorId(@PathVariable long authorId) {
        log.info("GET /api/v1/posts/users/{}/saved — request to get post drafts by author id: {}", authorId, authorId);
        return ResponseEntity.ok(postService.getPostsByAuthorId(authorId, false));
    }

    @GetMapping(PUBLISHED_BY_AUTHOR_ID_PATH)
    @Operation(
            summary = "Get user's published posts",
            description = "Returns all non-deleted published posts by user ID, " +
                    "sorted by publication date from newest to oldest"
    )
    public ResponseEntity<List<PostDto>> getPublishedPostsByAuthorId(@PathVariable long authorId) {
        log.info("GET /api/v1/posts/users/{}/published — request to get published posts " +
                "by author id: {}", authorId, authorId);
        return ResponseEntity.ok(postService.getPostsByAuthorId(authorId, true));
    }

    @GetMapping(NOT_PUBLISHED_BY_PROJECT_ID_PATH)
    @Operation(
            summary = "Get project's post drafts",
            description = "Returns all non-deleted post drafts by project ID, " +
                    "sorted by creation date from newest to oldest"
    )
    public ResponseEntity<List<PostDto>> getSavedPostsByProjectId(@PathVariable long projectId) {
        log.info("GET /api/v1/posts/projects/{}/saved — request to get post drafts " +
                "by project id: {}", projectId, projectId);
        return ResponseEntity.ok(postService.getPostsByProjectId(projectId, false));
    }

    @GetMapping(PUBLISHED_BY_PROJECT_ID_PATH)
    @Operation(
            summary = "Get project's published posts",
            description = "Returns all non-deleted published posts by project ID, " +
                    "sorted by publication date from newest to oldest"
    )
    public ResponseEntity<List<PostDto>> getPublishedPostsByProjectId(@PathVariable long projectId) {
        log.info("GET /api/v1/posts/projects/{}/published — request to get published posts " +
                "by project id: {}", projectId, projectId);
        return ResponseEntity.ok(postService.getPostsByProjectId(projectId, true));
    }
}
