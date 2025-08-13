package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;


    @PostMapping("/draft")
    public ResponseEntity<Void> createDraft(@RequestBody @Validated PostDraftDto postDraftDto) {
        postService.createPostDraft(postDraftDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/publishPost")
    public ResponseEntity<Void> publishPost(@RequestBody(required = false) @Validated PostDraftDto postDraftDto,
                                            @RequestParam(required = false) Long postId) {
        postService.publishPost(postId, postDraftDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/markedPostAsDeleted/{postId}")
    public ResponseEntity<PostDto> markPostAsDeleted(@PathVariable @NotNull(message = "PostId не может быть равен null") Long postId) {
        PostDto postDto = postService.markPostAsDeleted(postId);
        return ResponseEntity.ok(postDto);
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<PostDto> update(@RequestBody @Validated PostDto postDto, @PathVariable @NotNull(message = "PostId не может быть равен null") Long postId) {
        PostDto result = postService.updatePost(postDto, postId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable @NotNull(message = "PostId не может быть равен null") Long postId) {
        PostDto post = postService.findById(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/byAuthor/{userId}")
    public ResponseEntity<List<PostDto>> getPostsByAuthor(@PathVariable @NotNull(message = "UserId не может быть равен null") Long userId) {
        List<PostDto> allPostsByAuthorId = postService.getAllPostsByAuthorId(userId);
        return ResponseEntity.ok(allPostsByAuthorId);
    }

    @GetMapping("/byProject/{projectId}")
    public ResponseEntity<List<PostDto>> getPostsByProject(@PathVariable @NotNull(message = "ProjectId не может быть равен null") Long projectId) {
        List<PostDto> allPostsByProjectId = postService.getAllPostsByProjectId(projectId);
        return ResponseEntity.ok(allPostsByProjectId);
    }

    @GetMapping("/publishedPostsByAuthor/{userId}")
    public ResponseEntity<List<PostDto>> getPublishedPostsByAuthor(@PathVariable @NotNull(message = "UserId не может быть равен null") Long userId) {
        List<PostDto> allPublishedPostsByAuthorId = postService.getAllPublishedPostsByAuthorId(userId);
        return ResponseEntity.ok(allPublishedPostsByAuthorId);
    }

    @GetMapping("/publishedPostsByProject/{projectId}")
    public ResponseEntity<List<PostDto>> getPublishedPostsByProject(@PathVariable @NotNull(message = "ProjectId не может быть равен null") Long projectId) {
        List<PostDto> allPublishedPostsByProjectId = postService.getAllPublishedPostsByProjectId(projectId);
        return ResponseEntity.ok(allPublishedPostsByProjectId);
    }
}
