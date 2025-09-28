package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostDto create(@RequestBody @Valid PostDto post) {
        return postService.create(post);
    }

    @PatchMapping("/{id}/publish")
    public void publish(@PathVariable long id) {
        postService.publish(id);
    }
}
