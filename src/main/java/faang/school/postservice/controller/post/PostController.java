package faang.school.postservice.controller.post;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;

    @PutMapping("/{postId}/content")
    public void correctionTextInPost(@PathVariable("postId") Long postId) {
        postService.correctionTextInPost(postId);
    }

}
