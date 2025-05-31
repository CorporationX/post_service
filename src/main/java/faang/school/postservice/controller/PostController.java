package faang.school.postservice.controller;

import faang.school.postservice.dto.newsfeed.post.CreatePostRequest;
import faang.school.postservice.dto.newsfeed.post.PostResponseDto;
import faang.school.postservice.service.PostCorrecter;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostCorrecter postCorrecter;
    private final PostService postService;

    @PostMapping("/check-grammar")
    public ResponseEntity<String> correctUnpublishedPosts() {
        postCorrecter.correctUnpublishedPosts();
        return ResponseEntity.ok().body("Posts have been spell checked");
    }

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody CreatePostRequest request) {
        PostResponseDto createdPost = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);

    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> viewPost(@PathVariable Long postId) {
        PostResponseDto postDto = postService.getPostForUserViewing(postId);
        return ResponseEntity.ok(postDto);
    }
}
