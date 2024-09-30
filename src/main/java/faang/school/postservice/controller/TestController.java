package faang.school.postservice.controller;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final PostService postService;

    @GetMapping("/test")
    public void test() {
        postService.correctSpellingInUnpublishedPosts();
    }
}
