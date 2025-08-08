package faang.school.postservice.controller.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.kafka.producer.KafkaPostViewProducer;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final KafkaPostViewProducer kafkaPostViewProducer;
    private final UserContext userContext;


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
        PostDto postDto = postService.getById(postId);
        try {
            Long userId = userContext.getUserId();
            if (userId != null) {
                kafkaPostViewProducer.sendViewEvent(userId, postId);
            }
        } catch (Exception e) {
            log.warn("Failed to send view event for postId={} userId={}", postId, userContext.getUserId(), e);
        }
        return postDto;
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
