package faang.school.postservice.controller.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreateRequestDto;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostController {
    private final PostFacade postFacade;
    private final UserServiceClient userServiceClient;
    @PostMapping("/draft")
    public ResponseEntity<PostResponseDto> createDraftPost
            (@RequestBody @Valid PostCreateRequestDto postCreateRequestDto) {
        log.info("Post controller accepted request create draft post {}", postCreateRequestDto);

        PostResponseDto response = postFacade.createDraftPost(postCreateRequestDto);
        log.info("Post controller return response create draft post {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED) ;
    }

    @PatchMapping("/{postId}/publish")
    public ResponseEntity<PostResponseDto> publishPost(@PathVariable long postId) {
        log.info("Post controller accepted request publish post with id {}", postId);

        PostResponseDto response = postFacade.publishPost(postId);
        log.info("Post controller return response publish post {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable long postId,
                                                      @RequestBody @Valid PostUpdateRequestDto postUpdateRequestDto) {
        log.info("Post controller accepted request update post with id {}", postId);

        PostResponseDto response = postFacade.updatePost(postId, postUpdateRequestDto);
        log.info("Post controller return response update post {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable long postId) {
        log.info("Post controller accepted request delete post with id {}", postId);

        postFacade.deletePost(postId);
        log.info("Post controller return response delete post with id {}", postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable long postId) {
        log.info("Post controller accepted request get post with id {}", postId);

        PostResponseDto response = postFacade.getPostById(postId);
        log.info("Post controller return response get post {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/draft/user")
    public ResponseEntity<List<PostResponseDto>> getAllDraftPostsForUser() {
        log.info("Post controller accepted request get all draft posts by user");

        List<PostResponseDto> response = postFacade.getAllDraftPostsForUser();
        log.info("Post controller return response get all draft posts by user {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/draft/project/{projectId}")
    public ResponseEntity<List<PostResponseDto>> getAllDraftPostsByProjectId(@PathVariable long projectId) {
        log.info("Post controller accepted request get all draft posts by project with id {}", projectId);

        List<PostResponseDto> response = postFacade.getAllDraftPostsByProjectId(projectId);
        log.info("Post controller return response get all draft posts by project {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/published/user")
    public ResponseEntity<List<PostResponseDto>> getAllPublishedPostsForUser() {
        log.info("Post controller accepted request get all published posts by user");

        List<PostResponseDto> response = postFacade.getAllPublishedPostsForUser();
        log.info("Post controller return response get all published posts by user {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/published/project/{projectId}")
    public ResponseEntity<List<PostResponseDto>> getAllPublishedPostsByProjectId(@PathVariable long projectId) {
        log.info("Post controller accepted request get all published posts by project with id {}", projectId);

        List<PostResponseDto> response = postFacade.getAllPublishedPostsByProjectId(projectId);
        log.info("Post controller return response get all published posts by project {}", response);
        return ResponseEntity.ok(response);
    }
}
