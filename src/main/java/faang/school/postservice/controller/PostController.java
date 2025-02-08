package faang.school.postservice.controller;

import faang.school.postservice.dto.posts.PostDto;
import faang.school.postservice.dto.posts.PostSaveDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<PostDto> create(@NotNull @RequestBody PostSaveDto postSaveDto) {
        postValidator.validatePost(postSaveDto);
        return new ResponseEntity<>(postService.create(postSaveDto), HttpStatus.CREATED);
    }

    @GetMapping(ID_PATH)
    public ResponseEntity<PostDto> getPost(@PathVariable long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @PutMapping(ID_PATH)
    public ResponseEntity<PostDto> update(@PathVariable long id, @NotNull @RequestBody PostSaveDto postSaveDto) {
        postValidator.validatePost(postSaveDto);
        return ResponseEntity.ok(postService.update(id, postSaveDto));
    }


    @PostMapping(PUBLISH_ID_PATH)
    public ResponseEntity<String> publish(@PathVariable long id) {
        postService.publish(id);
        return ResponseEntity.ok("Пост успешно опубликован!");
    }

    @DeleteMapping(ID_PATH)
    public ResponseEntity<String> delete(@PathVariable long id) {
        postService.delete(id);
        return ResponseEntity.ok("Пост успешно удален!");
    }

    @GetMapping(NOT_PUBLISHED_BY_AUTHOR_ID_PATH)
    public ResponseEntity<List<PostDto>> getSavedPostsByAuthorId(@PathVariable long authorId) {
        return ResponseEntity.ok(postService.getPostsByAuthorId(authorId, false));
    }

    @GetMapping(PUBLISHED_BY_AUTHOR_ID_PATH)
    public ResponseEntity<List<PostDto>> getPublishedPostsByAuthorId(@PathVariable long authorId) {
        return ResponseEntity.ok(postService.getPostsByAuthorId(authorId, true));
    }

    @GetMapping(NOT_PUBLISHED_BY_PROJECT_ID_PATH)
    public ResponseEntity<List<PostDto>> getSavedPostsByProjectId(@PathVariable long projectId) {
        return ResponseEntity.ok(postService.getPostsByProjectId(projectId, false));
    }

    @GetMapping(PUBLISHED_BY_PROJECT_ID_PATH)
    public ResponseEntity<List<PostDto>> getPublishedPostsByProjectId(@PathVariable long projectId) {
        return ResponseEntity.ok(postService.getPostsByProjectId(projectId, true));
    }
}
