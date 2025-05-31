package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostCreateProjectRequestDto;
import faang.school.postservice.dto.post.PostCreateUserRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.facade.post.PostFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostController {
    private final PostFacade postFacade;

    @PostMapping("/draft/user/me")
    public ResponseEntity<PostResponseDto> createDraftPostForCurrentUser
            (@RequestBody @Valid PostCreateUserRequestDto postCreateUserRequestDto) {
        log.debug("Post controller accepted request create draft post for user {}", postCreateUserRequestDto);

        PostResponseDto response = postFacade.createDraftPostForCurrentUser(postCreateUserRequestDto);
        log.debug("Post controller return response create draft post for user {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/draft/project/{projectId}")
    public ResponseEntity<PostResponseDto> createDraftPostForProject
            (@RequestBody @Valid PostCreateProjectRequestDto postCreateProjectRequestDto) {
        log.debug("Post controller accepted request create draft post for project {}", postCreateProjectRequestDto);

        PostResponseDto response = postFacade.createDraftPostForProject(postCreateProjectRequestDto);
        log.debug("Post controller return response create draft post for project {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{postId}/publish")
    public ResponseEntity<PostResponseDto> publishPost(@PathVariable long postId) {
        log.debug("Post controller accepted request publish post with id {}", postId);

        PostResponseDto response = postFacade.publishPost(postId);
        log.debug("Post controller return response publish post {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable long postId,
                                                      @RequestBody @Valid PostUpdateRequestDto postUpdateRequestDto) {
        log.debug("Post controller accepted request update post with id {}", postId);

        PostResponseDto response = postFacade.updatePost(postId, postUpdateRequestDto);
        log.debug("Post controller return response update post {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable long postId) {
        log.debug("Post controller accepted request delete post with id {}", postId);

        postFacade.deletePost(postId);
        log.debug("Post controller return response delete post with id {}", postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable long postId) {
        log.debug("Post controller accepted request get post with id {}", postId);

        PostResponseDto response = postFacade.getPostById(postId);
        log.debug("Post controller return response get post {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/drafts/user")
    public ResponseEntity<List<PostResponseDto>> getAllDraftPostsByUserId(@RequestParam Long userId) {
        log.debug("Post controller accepted request get all draft posts by user id {}", userId);


        List<PostResponseDto> response = postFacade.getAllDraftPostsByUserId(userId);
        log.debug("Post controller return response get all draft posts for user with id {} {}", userId, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/drafts/project")
    public ResponseEntity<List<PostResponseDto>> getAllDraftPostsByProjectId(@RequestParam Long projectId) {
        log.debug("Post controller accepted request get all draft posts by project id {}", projectId);

        List<PostResponseDto> response = postFacade.getAllDraftPostsByProjectId(projectId);
        log.debug("Post controller return response get all draft posts for project with id {} {}",
                projectId, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/published/user")
    public ResponseEntity<List<PostResponseDto>> getAllPublishedPostsByUserId(@RequestParam Long userId) {
        log.debug("Post controller accepted request get all published posts by user id {}", userId);

        List<PostResponseDto> response = postFacade.getAllPublishedPostsByUserId(userId);
        log.debug("Post controller return response get all published posts for user with id {} {}", userId, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/published/project")
    public ResponseEntity<List<PostResponseDto>> getAllPublishedPosts(@RequestParam Long projectId) {
        log.debug("Post controller accepted request get all published posts by project id {}", projectId);

        List<PostResponseDto> response = postFacade.getAllPublishedPostsByProjectId(projectId);
        log.debug("Post controller return response get all published posts for project with id {} {}",
                projectId, response);
        return ResponseEntity.ok(response);
    }
}
