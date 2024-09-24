package faang.school.postservice.controller;

import faang.school.postservice.service.PostViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/views")
@RequiredArgsConstructor
public class PostViewController {
    private final PostViewService postViewService;

    @PostMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public void viewPost(
        @PathVariable long postId,
        @RequestParam long userId
    ) {
        postViewService.handlePostView(postId, userId);
    }
}
