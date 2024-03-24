package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserContext userContext;

    @PostMapping("/draft")
    public void createPostDraft(@RequestPart @Valid PostDto dto) {
        postService.createPostDraft(dto);
    }

    @PostMapping("/{postId}/video")
    public ResponseEntity<String> uploadVideo(@PathVariable long postId,
                                              @RequestPart("files") @Size(max = 5) List<MultipartFile> files) {
        List<ResourceDto> resourceDtos = postService.addVideo(postId, files);
        return ResponseEntity.ok("Files uploaded: " + resourceDtos);
    }

    @DeleteMapping("/{postId}/video")
    public void deleteVideos(@PathVariable long postId,
                             @RequestPart List<Long> resourceIds) {
        postService.deleteVideo(postId, resourceIds);
    }

    @PostMapping("/publish/{postId}")
    public void publishPost(@PathVariable long postId) {
        postService.publishPost(postId, userContext.getUserId());
    }

    @PutMapping("/{postId}")
    public void updatePost(@PathVariable long postId, @RequestBody PostDto dto) {
        postService.updatePost(postId, userContext.getUserId(), dto);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable long postId) {
        postService.deletePost(postId, userContext.getUserId());
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/author/drafts/{authorId}")
    public List<PostDto> getAuthorDrafts(@PathVariable long authorId) {
        return postService.getAuthorDrafts(authorId);
    }

    @GetMapping("/project/drafts/{projectId}")
    public List<PostDto> getProjectDrafts(@PathVariable long projectId) {
        return postService.getProjectDrafts(projectId);
    }

    @GetMapping("/author/posts/{authorId}")
    public List<PostDto> getAuthorPosts(@PathVariable long authorId) {
        return postService.getAuthorPosts(authorId);
    }

    @GetMapping("/project/posts/{projectId}")
    public List<PostDto> getProjectPosts(@PathVariable long projectId) {
        return postService.getProjectPosts(projectId);
    }

}
