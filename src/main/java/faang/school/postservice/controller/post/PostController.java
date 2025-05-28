package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.exception.validation.ValidationRequestException;
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
    @PostMapping("/draft")
    public ResponseEntity<PostResponseDto> createDraftPost
            (@RequestBody @Valid PostCreateRequestDto postCreateRequestDto) {
        log.debug("Post controller accepted request create draft post {}", postCreateRequestDto);

        PostResponseDto response = postFacade.createDraftPost(postCreateRequestDto);
        log.debug("Post controller return response create draft post {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED) ;
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

    @GetMapping("/draft")
    public ResponseEntity<List<PostResponseDto>> getAllDraftPosts(@RequestParam(required = false) Long userId,
                                                                  @RequestParam(required = false) Long projectId) {
        log.debug("Post controller accepted request get all draft posts by user id {} or project id {}",
                userId, projectId);

        if ((userId == null && projectId == null) || (userId != null && projectId != null)) {
            throw new ValidationRequestException("Exactly one of userId or projectId must be provided");
        }

        List<PostResponseDto> response = postFacade.getAllDraftPosts(userId, projectId);
        log.debug("Post controller return response get all draft posts {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/published")
    public ResponseEntity<List<PostResponseDto>> getAllPublishedPosts(@RequestParam(required = false) Long userId,
                                                                      @RequestParam(required = false) Long projectId) {
        log.debug("Post controller accepted request get all published posts by user id {} or project id {}",
                userId, projectId);

        if ((userId == null && projectId == null) || (userId != null && projectId != null)) {
            throw new ValidationRequestException("Exactly one of userId or projectId must be provided");
        }

        List<PostResponseDto> response = postFacade.getAllPublishedPosts(userId, projectId);
        log.debug("Post controller return response get all published posts {}", response);
        return ResponseEntity.ok(response);
    }
}
