package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostRequest;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostCorrecter;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@Valid @RequestBody CreatePostRequest request) {
        Post createdPost = postService.createPost(request);
        return ResponseEntity.ok(createdPost);
    }
}
