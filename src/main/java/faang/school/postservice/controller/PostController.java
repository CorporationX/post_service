package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class PostController {

    private final PostService postService;

    @PostMapping("posts/create")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        Post post = PostMapper.PostDtoToPost(postDto);
        postService.createPost(post);
        return new ResponseEntity<>(
                postDto,
                HttpStatus.OK
        );
    }

    @PutMapping("posts/{postId}/publish")
    public ResponseEntity<Long> publishPost(@PathVariable Long postId) {
        postService.publishPost(postId);
        return new ResponseEntity<>(
                postId,
                HttpStatus.OK
        );
    }

    @PutMapping("posts/{postId}/update")
    public ResponseEntity<Long> updatePost(@PathVariable Long postId, @RequestParam String content,
                                           @Nullable @RequestParam LocalDateTime scheduledAt) {
        postService.updatePost(postId, content, scheduledAt);
        return new ResponseEntity<>(
                postId,
                HttpStatus.OK
        );
    }

    @PutMapping("posts/{postId}/delete")
    public ResponseEntity<Long> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return new ResponseEntity<>(
                postId,
                HttpStatus.OK
        );
    }

    @GetMapping("posts/{postId}/get")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        return new ResponseEntity<>(
                post,
                HttpStatus.OK
        );
    }

    @GetMapping("/{userId}/posts/get/drafts")
    public ResponseEntity<List<Post>> getNotDeletedDraftsByUserId(@PathVariable Long userId) {
        List<Post> postList = postService.getNotDeletedDraftsByUserId(userId);
        return new ResponseEntity<>(
                postList,
                HttpStatus.OK
        );
    }

    @GetMapping("/posts/get/drafts/{projectId}")
    public ResponseEntity<List<Post>> getNotDeletedDraftsByProjectId(@PathVariable Long projectId) {
        List<Post> postList = postService.getNotDeletedDraftsByProjectId(projectId);
        return new ResponseEntity<>(
                postList,
                HttpStatus.OK
        );
    }

    @GetMapping("/{userId}/posts/get/published")
    public ResponseEntity<List<Post>> getNotDeletedPublishedByUserId(@PathVariable Long userId) {
        List<Post> postList = postService.getNotDeletedPublishedByUserId(userId);
        return new ResponseEntity<>(
                postList,
                HttpStatus.OK
        );
    }

    @GetMapping("/posts/get/published/{projectId}")
    public ResponseEntity<List<Post>> getNotDeletedPublishedByProjectId(@PathVariable Long projectId) {
        List<Post> postList = postService.getNotDeletedPublishedByProjectId(projectId);
        return new ResponseEntity<>(
                postList,
                HttpStatus.OK
        );
    }

}
