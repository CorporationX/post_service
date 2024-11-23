package faang.school.postservice.controller.test;

import faang.school.postservice.model.post.CacheablePost;
import faang.school.postservice.repository.post.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test/post")
@RequiredArgsConstructor
public class PostTestController {
    private final PostCacheRepository postCacheRepository;

    @GetMapping("/{postId}")
    public CacheablePost getPost(@PathVariable("postId") long postId) {
        return postCacheRepository.findById(postId).orElse(null);
    }
}
