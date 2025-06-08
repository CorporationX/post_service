package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

@Validated
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostDto createPost(@RequestBody @Valid CreatePostDto createPostDto) {
        return postService.create(createPostDto);
    }

    @PatchMapping("/{postId}/publish")
    public PostDto publishPost(@PathVariable @NotNull @Positive Long postId) {
        return postService.publishPost(postId);
    }

    @PatchMapping("/{postId}")
    public PostDto updatePost(@PathVariable @NotNull @Positive Long postId,
                              @RequestParam @Valid String content) {
        return postService.updateContent(postId, content);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable @NotNull @Positive Long postId) {
        postService.delete(postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    public PostDto getById(@PathVariable @NotNull @Positive Long postId) {
        return postService.getById(postId);
    }

    @GetMapping("/scratches/by_author")
    public List<PostDto> getNonDeletedScratchesByAuthorId(@RequestParam @NotNull @Positive Long authorId) {
        return postService.getNonDeletedScratchesByAuthorId(authorId);
    }

    @GetMapping("/scratches/by_project")
    public List<PostDto> getNonDeletedScratchesByProjectId(@RequestParam @NotNull @Positive Long projectId) {
        return postService.getNonDeletedScratchesByProjectId(projectId);
    }

    @GetMapping("/published/by_author")
    public List<PostDto> getNonDeletedPublishedByAuthorId(@RequestParam @NotNull @Positive Long authorId) {
        return postService.getNonDeletedPublishedByAuthorId(authorId);
    }

    @GetMapping("/published/by_project")
    public List<PostDto> getNonDeletedPublishedByProjectId(@RequestParam @NotNull @Positive Long projectId) {
        return postService.getNonDeletedPublishedByProjectId(projectId);
    }
}
